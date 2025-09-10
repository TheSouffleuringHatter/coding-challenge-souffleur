package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import jakarta.enterprise.inject.Instance;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import windowskeyboardhook.WindowsKeyEvent;

@ExtendWith(MockitoExtension.class)
class KeyHandlerProcessorTest {

  @Mock private Instance<KeyHandler> keyHandlerInstances;
  @Mock private PlatformRunLater platformRunLater;
  @Mock private KeyHandler mockKeyHandler;
  @Mock private WindowsKeyEvent mockEvent;

  private KeyHandlerProcessor processor;

  private static Stream<Arguments> responsibleFor_testData() {
    return Stream.of(
        // Format: handlerKeyCode, eventKeyCode, modifiersMatch, expectedResult
        Arguments.of(Win32VK.VK_A, Win32VK.VK_A, true, true), // Both key code and modifiers match
        Arguments.of(
            Win32VK.VK_A, Win32VK.VK_A, false, false), // Key code matches but modifiers don't
        Arguments.of(
            Win32VK.VK_B, Win32VK.VK_A, true, false), // Modifiers match but key code doesn't
        Arguments.of(
            Win32VK.VK_B, Win32VK.VK_A, false, false) // Neither key code nor modifiers match
        );
  }

  private static Stream<Arguments> consume_testData() {
    return Stream.of(
        // Format: handlerKeyCode, eventKeyCode, keyDown, expectedInvocation, expectedResult
        Arguments.of(Win32VK.VK_A, Win32VK.VK_A, true, true, true), // Key matches, key down
        Arguments.of(Win32VK.VK_A, Win32VK.VK_A, false, false, true), // Key matches, key not down
        Arguments.of(Win32VK.VK_A, Win32VK.VK_B, true, false, false), // Key doesn't match
        Arguments.of(
            Win32VK.VK_A, Win32VK.VK_B, false, false, false) // Key doesn't match, key not down
        );
  }

  @BeforeEach
  void setUp() {
    // Setup the mock to return a handler with VK_A by default
    when(mockKeyHandler.getKeyCode()).thenReturn(Win32VK.VK_A);

    // Mock PlatformRunLater to immediately execute runnables
    lenient()
        .doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(platformRunLater)
        .accept(any(Runnable.class));

    lenient()
        .doAnswer(
            invocation -> {
              Consumer<KeyHandler> consumer = invocation.getArgument(0);
              consumer.accept(mockKeyHandler);
              return null;
            })
        .when(keyHandlerInstances)
        .forEach(any(Consumer.class));

    processor =
        new KeyHandlerProcessor(
            keyHandlerInstances, platformRunLater, List.of(Win32VK.VK_RCONTROL));
  }

  /**
   * Tests the {@code responsibleFor} method in the {@code KeyHandlerProcessor} class using
   * parameterized test cases to cover different combinations of key codes and modifier states.
   */
  @ParameterizedTest(name = "handlerKey={0}, eventKey={1}, modifiersMatch={2} -> expected={3}")
  @MethodSource("responsibleFor_testData")
  void responsibleFor_shouldReturnExpectedResult(
      final Win32VK handlerKeyCode,
      final Win32VK eventKeyCode,
      final boolean modifiersMatch,
      final boolean expectedResult) {

    // Given - create a new processor with a handler for the specific key
    var specificKeyHandler = mock(KeyHandler.class);
    when(specificKeyHandler.getKeyCode()).thenReturn(handlerKeyCode);

    lenient()
        .doAnswer(
            invocation -> {
              Consumer<KeyHandler> consumer = invocation.getArgument(0);
              consumer.accept(specificKeyHandler);
              return null;
            })
        .when(keyHandlerInstances)
        .forEach(any(Consumer.class));

    // Mock PlatformRunLater to immediately execute runnables
    lenient()
        .doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(platformRunLater)
        .accept(any(Runnable.class));

    processor =
        new KeyHandlerProcessor(
            keyHandlerInstances, platformRunLater, List.of(Win32VK.VK_RCONTROL));

    lenient().when(mockEvent.keyCode()).thenReturn(eventKeyCode);
    lenient()
        .when(mockEvent.pressedModifierKeyCodes())
        .thenReturn(modifiersMatch ? Set.of(Win32VK.VK_RCONTROL.code) : Set.of());

    // When
    var result = processor.responsibleFor(mockEvent);

    // Then
    assertEquals(expectedResult, result);
  }

  /**
   * Tests the {@code consume} method in the {@code KeyHandlerProcessor} class using parameterized
   * test cases to cover different scenarios of key and event behavior.
   */
  @ParameterizedTest(
      name = "handlerKey={0}, eventKey={1}, keyDown={2} -> expectedInvocation={3}, expected={4}")
  @MethodSource("consume_testData")
  void consume_shouldReturnExpectedResult(
      final Win32VK handlerKeyCode,
      final Win32VK eventKeyCode,
      final boolean keyDown,
      final boolean expectedInvocation,
      final boolean expectedResult) {

    // Given - create a new processor with a handler for the specific key
    var specificKeyHandler = mock(KeyHandler.class);
    when(specificKeyHandler.getKeyCode()).thenReturn(handlerKeyCode);

    lenient()
        .doAnswer(
            invocation -> {
              Consumer<KeyHandler> consumer = invocation.getArgument(0);
              consumer.accept(specificKeyHandler);
              return null;
            })
        .when(keyHandlerInstances)
        .forEach(any(Consumer.class));

    // Mock PlatformRunLater to immediately execute runnables
    lenient()
        .doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(platformRunLater)
        .accept(any(Runnable.class));

    processor =
        new KeyHandlerProcessor(
            keyHandlerInstances, platformRunLater, List.of(Win32VK.VK_RCONTROL));

    lenient().when(mockEvent.keyCode()).thenReturn(eventKeyCode);
    lenient().when(mockEvent.keyDown()).thenReturn(keyDown);

    // When
    var result = processor.consume(mockEvent);

    // Then
    assertEquals(expectedResult, result);
    if (expectedInvocation) {
      verify(specificKeyHandler).performAction();
    } else {
      verify(specificKeyHandler, never()).performAction();
    }
  }

  @Test
  void responsibleFor_withMultipleHandlers_shouldFindCorrectHandler() {
    // Given
    var handlerA = mock(KeyHandler.class);
    var handlerB = mock(KeyHandler.class);

    when(handlerA.getKeyCode()).thenReturn(Win32VK.VK_A);
    when(handlerB.getKeyCode()).thenReturn(Win32VK.VK_B);

    lenient()
        .doAnswer(
            invocation -> {
              Consumer<KeyHandler> consumer = invocation.getArgument(0);
              consumer.accept(handlerA);
              consumer.accept(handlerB);
              return null;
            })
        .when(keyHandlerInstances)
        .forEach(any(Consumer.class));

    // Mock PlatformRunLater to immediately execute runnables
    lenient()
        .doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(platformRunLater)
        .accept(any(Runnable.class));

    processor =
        new KeyHandlerProcessor(
            keyHandlerInstances, platformRunLater, List.of(Win32VK.VK_RCONTROL));

    when(mockEvent.keyCode()).thenReturn(Win32VK.VK_B);
    when(mockEvent.pressedModifierKeyCodes()).thenReturn(Set.of(Win32VK.VK_RCONTROL.code));

    // When
    var result = processor.responsibleFor(mockEvent);

    // Then
    assertTrue(result);
  }

  @Test
  void consume_withMultipleHandlers_shouldInvokeCorrectHandler() {
    // Given
    var handlerA = mock(KeyHandler.class);
    var handlerB = mock(KeyHandler.class);

    when(handlerA.getKeyCode()).thenReturn(Win32VK.VK_A);
    when(handlerB.getKeyCode()).thenReturn(Win32VK.VK_B);

    lenient()
        .doAnswer(
            invocation -> {
              Consumer<KeyHandler> consumer = invocation.getArgument(0);
              consumer.accept(handlerA);
              consumer.accept(handlerB);
              return null;
            })
        .when(keyHandlerInstances)
        .forEach(any(Consumer.class));

    // Mock PlatformRunLater to immediately execute runnables
    lenient()
        .doAnswer(
            invocation -> {
              Runnable runnable = invocation.getArgument(0);
              runnable.run();
              return null;
            })
        .when(platformRunLater)
        .accept(any(Runnable.class));

    processor =
        new KeyHandlerProcessor(
            keyHandlerInstances, platformRunLater, List.of(Win32VK.VK_RCONTROL));

    when(mockEvent.keyCode()).thenReturn(Win32VK.VK_B);
    when(mockEvent.keyDown()).thenReturn(true);

    // When
    var result = processor.consume(mockEvent);

    // Then
    assertTrue(result);
    verify(handlerA, never()).performAction();
    verify(handlerB).performAction();
  }
}
