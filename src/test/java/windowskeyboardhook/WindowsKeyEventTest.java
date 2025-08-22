package windowskeyboardhook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.jna.platform.win32.Win32VK;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WindowsKeyEventTest {

  static Stream<Arguments> modifierKeyCodesMatchExactlyTestCases() {
    return Stream.of(
        Arguments.of(
            "with exact keys should return true",
            Set.of(Win32VK.VK_SHIFT.code, Win32VK.VK_CONTROL.code),
            new Win32VK[] {Win32VK.VK_SHIFT, Win32VK.VK_CONTROL},
            true),
        Arguments.of(
            "with additional key should return false",
            Set.of(Win32VK.VK_SHIFT.code, Win32VK.VK_CONTROL.code),
            new Win32VK[] {Win32VK.VK_SHIFT},
            false),
        Arguments.of(
            "with missing key should return false",
            Set.of(Win32VK.VK_SHIFT.code),
            new Win32VK[] {Win32VK.VK_SHIFT, Win32VK.VK_CONTROL},
            false),
        Arguments.of(
            "with empty event and empty input should return true",
            Set.of(),
            new Win32VK[] {},
            true),
        Arguments.of(
            "with empty event and non-empty input should return false",
            Set.of(),
            new Win32VK[] {Win32VK.VK_SHIFT},
            false),
        Arguments.of(
            "with non-empty event and empty input should return false",
            Set.of(Win32VK.VK_SHIFT.code),
            new Win32VK[] {},
            false),
        Arguments.of(
            "with unmatched key should return false",
            Set.of(Win32VK.VK_SHIFT.code),
            new Win32VK[] {Win32VK.VK_CONTROL},
            false));
  }

  @ParameterizedTest
  @MethodSource("modifierKeyCodesMatchExactlyTestCases")
  void modifierKeyCodesMatchExactly(
      final String testCase,
      final Set<Integer> eventModifierKeyCodes,
      final Win32VK[] inputKeys,
      final boolean expectedResult) {

    var event =
        new WindowsKeyEvent(
            Win32VK.VK_A,
            eventModifierKeyCodes,
            !eventModifierKeyCodes.isEmpty()
                && eventModifierKeyCodes.contains(Win32VK.VK_SHIFT.code),
            !eventModifierKeyCodes.isEmpty()
                && eventModifierKeyCodes.contains(Win32VK.VK_CONTROL.code),
            false,
            !eventModifierKeyCodes.isEmpty()
                && eventModifierKeyCodes.contains(Win32VK.VK_SHIFT.code),
            !eventModifierKeyCodes.isEmpty()
                && eventModifierKeyCodes.contains(Win32VK.VK_CONTROL.code),
            false,
            false,
            false,
            false,
            true,
            false);

    var result = event.modifierKeyCodesMatchExactly(inputKeys);

    assertEquals(expectedResult, result, testCase);
  }
}
