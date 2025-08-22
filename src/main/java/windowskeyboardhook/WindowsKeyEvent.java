package windowskeyboardhook;

import com.sun.jna.platform.win32.Win32VK;
import java.util.Arrays;
import java.util.Set;

/** Represents a Windows keyboard event with information about the key and modifier states. */
public record WindowsKeyEvent(
    Win32VK keyCode,
    Set<Integer> pressedModifierKeyCodes,
    boolean anyShiftPressed,
    boolean leftShiftPressed,
    boolean rightShiftPressed,
    boolean anyCtrlPressed,
    boolean leftCtrlPressed,
    boolean rightCtrlPressed,
    boolean anyAltPressed,
    boolean leftAltPressed,
    boolean rightAltPressed,
    boolean keyDown,
    boolean keyUp) {

  public WindowsKeyEvent {
    pressedModifierKeyCodes = Set.copyOf(pressedModifierKeyCodes);
  }

  public boolean modifierKeyCodesMatchExactly(final Win32VK... keyCodes) {
    return pressedModifierKeyCodes.size() == keyCodes.length
        && Arrays.stream(keyCodes)
            .allMatch(keyCode -> pressedModifierKeyCodes.contains(keyCode.code));
  }
}
