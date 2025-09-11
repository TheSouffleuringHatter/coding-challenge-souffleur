package windowskeyboardhook;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinUser;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyboardStateManagerTest {

  private KeyboardStateManager keyboardStateManager;

  @BeforeEach
  void setUp() {
    keyboardStateManager = new KeyboardStateManager();
  }

  @Test
  void shouldIdentifyKeyDownEvents() {
    assertTrue(keyboardStateManager.isKeyDownEvent(WinUser.WM_KEYDOWN));
    assertTrue(keyboardStateManager.isKeyDownEvent(WinUser.WM_SYSKEYDOWN));
    assertFalse(keyboardStateManager.isKeyDownEvent(WinUser.WM_KEYUP));
    assertFalse(keyboardStateManager.isKeyDownEvent(WinUser.WM_SYSKEYUP));
  }

  @Test
  void shouldIdentifyKeyUpEvents() {
    assertTrue(keyboardStateManager.isKeyUpEvent(WinUser.WM_KEYUP));
    assertTrue(keyboardStateManager.isKeyUpEvent(WinUser.WM_SYSKEYUP));
    assertFalse(keyboardStateManager.isKeyUpEvent(WinUser.WM_KEYDOWN));
    assertFalse(keyboardStateManager.isKeyUpEvent(WinUser.WM_SYSKEYDOWN));
  }

  @Test
  void shouldInitializeWithNoModifiersPressed() {
    assertFalse(keyboardStateManager.isLeftShiftPressed());
    assertFalse(keyboardStateManager.isRightShiftPressed());
    assertFalse(keyboardStateManager.isLeftCtrlPressed());
    assertFalse(keyboardStateManager.isRightCtrlPressed());
    assertFalse(keyboardStateManager.isLeftAltPressed());
    assertFalse(keyboardStateManager.isRightAltPressed());
    assertFalse(keyboardStateManager.isAnyModifierPressed());
  }

  @Test
  void shouldReturnEmptySetForPressedModifierKeyCodes() {
    Set<Integer> pressedKeys = keyboardStateManager.getPressedModifierKeyCodes();

    assertNotNull(pressedKeys);
    assertTrue(pressedKeys.isEmpty());
  }

  @Test
  void shouldReturnFalseForUnpressedModifierKey() {
    assertFalse(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LSHIFT));
    assertFalse(keyboardStateManager.isModifierKeyPressed(WinUser.VK_RCONTROL));
    assertFalse(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LMENU));
  }

  @Test
  void shouldProcessLeftShiftKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LSHIFT, WinUser.WM_KEYDOWN);

    assertTrue(keyboardStateManager.isLeftShiftPressed());
    assertFalse(keyboardStateManager.isRightShiftPressed());
    assertTrue(keyboardStateManager.isAnyShiftPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LSHIFT));
  }

  @Test
  void shouldProcessRightShiftKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_RSHIFT, WinUser.WM_KEYDOWN);

    assertFalse(keyboardStateManager.isLeftShiftPressed());
    assertTrue(keyboardStateManager.isRightShiftPressed());
    assertTrue(keyboardStateManager.isAnyShiftPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_RSHIFT));
  }

  @Test
  void shouldProcessLeftCtrlKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LCONTROL, WinUser.WM_KEYDOWN);

    assertTrue(keyboardStateManager.isLeftCtrlPressed());
    assertFalse(keyboardStateManager.isRightCtrlPressed());
    assertTrue(keyboardStateManager.isAnyCtrlPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LCONTROL));
  }

  @Test
  void shouldProcessRightCtrlKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_RCONTROL, WinUser.WM_KEYDOWN);

    assertFalse(keyboardStateManager.isLeftCtrlPressed());
    assertTrue(keyboardStateManager.isRightCtrlPressed());
    assertTrue(keyboardStateManager.isAnyCtrlPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_RCONTROL));
  }

  @Test
  void shouldProcessLeftAltKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LMENU, WinUser.WM_SYSKEYDOWN);

    assertTrue(keyboardStateManager.isLeftAltPressed());
    assertFalse(keyboardStateManager.isRightAltPressed());
    assertTrue(keyboardStateManager.isAnyAltPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LMENU));
  }

  @Test
  void shouldProcessRightAltKeyDown() {
    keyboardStateManager.processKeyEvent(WinUser.VK_RMENU, WinUser.WM_SYSKEYDOWN);

    assertFalse(keyboardStateManager.isLeftAltPressed());
    assertTrue(keyboardStateManager.isRightAltPressed());
    assertTrue(keyboardStateManager.isAnyAltPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.isModifierKeyPressed(WinUser.VK_RMENU));
  }

  @Test
  void shouldProcessKeyUpEvents() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LSHIFT, WinUser.WM_KEYDOWN);
    assertTrue(keyboardStateManager.isLeftShiftPressed());

    keyboardStateManager.processKeyEvent(WinUser.VK_LSHIFT, WinUser.WM_KEYUP);
    assertFalse(keyboardStateManager.isLeftShiftPressed());
    assertFalse(keyboardStateManager.isAnyShiftPressed());
    assertFalse(keyboardStateManager.isModifierKeyPressed(WinUser.VK_LSHIFT));
  }

  @Test
  void shouldTrackMultipleModifiersSimultaneously() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LSHIFT, WinUser.WM_KEYDOWN);
    keyboardStateManager.processKeyEvent(WinUser.VK_RCONTROL, WinUser.WM_KEYDOWN);

    assertTrue(keyboardStateManager.isLeftShiftPressed());
    assertTrue(keyboardStateManager.isRightCtrlPressed());
    assertTrue(keyboardStateManager.isAnyShiftPressed());
    assertTrue(keyboardStateManager.isAnyCtrlPressed());
    assertTrue(keyboardStateManager.isAnyModifierPressed());

    Set<Integer> pressedKeys = keyboardStateManager.getPressedModifierKeyCodes();
    assertEquals(2, pressedKeys.size());
    assertTrue(pressedKeys.contains(WinUser.VK_LSHIFT));
    assertTrue(pressedKeys.contains(WinUser.VK_RCONTROL));
  }

  @Test
  void shouldIgnoreNonModifierKeys() {
    keyboardStateManager.processKeyEvent(Win32VK.VK_ESCAPE.code, WinUser.WM_KEYDOWN);
    keyboardStateManager.processKeyEvent(Win32VK.VK_RETURN.code, WinUser.WM_KEYDOWN);

    assertFalse(keyboardStateManager.isAnyModifierPressed());
    assertTrue(keyboardStateManager.getPressedModifierKeyCodes().isEmpty());
  }

  @Test
  void shouldReturnImmutableCopyOfPressedKeys() {
    keyboardStateManager.processKeyEvent(WinUser.VK_LSHIFT, WinUser.WM_KEYDOWN);

    Set<Integer> pressedKeys = keyboardStateManager.getPressedModifierKeyCodes();
    assertThrows(UnsupportedOperationException.class, () -> pressedKeys.add(WinUser.VK_RSHIFT));
  }
}
