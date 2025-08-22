package windowskeyboardhook;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the state of keyboard modifier keys (Shift, Ctrl, Alt).
 *
 * <p>Tracks the state of each modifier key and provides methods to query their states.
 */
@ApplicationScoped
class KeyboardStateManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardStateManager.class);

  // Track all pressed modifier key codes
  private final Set<Integer> pressedModifierKeyCodes = ConcurrentHashMap.newKeySet();

  // Shift key states
  private final AtomicBoolean leftShiftDown = new AtomicBoolean(false);
  private final AtomicBoolean rightShiftDown = new AtomicBoolean(false);

  // Ctrl key states
  private final AtomicBoolean leftCtrlDown = new AtomicBoolean(false);
  private final AtomicBoolean rightCtrlDown = new AtomicBoolean(false);

  // Alt key states
  private final AtomicBoolean leftAltDown = new AtomicBoolean(false);
  private final AtomicBoolean rightAltDown = new AtomicBoolean(false);

  // Key identification methods
  private static boolean isLeftShiftKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_LSHIFT;
  }

  private static boolean isRightShiftKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_RSHIFT;
  }

  private static boolean isLeftCtrlKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_LCONTROL;
  }

  private static boolean isRightCtrlKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_RCONTROL;
  }

  private static boolean isLeftAltKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_LMENU;
  }

  private static boolean isRightAltKey(final int virtualKeyCode) {
    return virtualKeyCode == WinUser.VK_RMENU;
  }

  private static boolean isModifierKey(final int virtualKeyCode) {
    return isLeftShiftKey(virtualKeyCode)
        || isRightShiftKey(virtualKeyCode)
        || isLeftCtrlKey(virtualKeyCode)
        || isRightCtrlKey(virtualKeyCode)
        || isLeftAltKey(virtualKeyCode)
        || isRightAltKey(virtualKeyCode);
  }

  @PostConstruct
  void initializeFromSystem() {
    // Clear any existing state
    pressedModifierKeyCodes.clear();

    // Initialize shift key states
    var leftShiftPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_LSHIFT) < 0;
    var rightShiftPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_RSHIFT) < 0;
    leftShiftDown.set(leftShiftPressed);
    rightShiftDown.set(rightShiftPressed);
    if (leftShiftPressed) pressedModifierKeyCodes.add(WinUser.VK_LSHIFT);
    if (rightShiftPressed) pressedModifierKeyCodes.add(WinUser.VK_RSHIFT);

    // Initialize ctrl key states
    var leftCtrlPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_LCONTROL) < 0;
    var rightCtrlPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_RCONTROL) < 0;
    leftCtrlDown.set(leftCtrlPressed);
    rightCtrlDown.set(rightCtrlPressed);
    if (leftCtrlPressed) pressedModifierKeyCodes.add(WinUser.VK_LCONTROL);
    if (rightCtrlPressed) pressedModifierKeyCodes.add(WinUser.VK_RCONTROL);

    // Initialize alt key states
    var leftAltPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_LMENU) < 0;
    var rightAltPressed = User32.INSTANCE.GetAsyncKeyState(WinUser.VK_RMENU) < 0;
    leftAltDown.set(leftAltPressed);
    rightAltDown.set(rightAltPressed);
    if (leftAltPressed) pressedModifierKeyCodes.add(WinUser.VK_LMENU);
    if (rightAltPressed) pressedModifierKeyCodes.add(WinUser.VK_RMENU);

    LOGGER.trace(
        "Initial modifier states - Shift: {} {}, Ctrl: {} {}, Alt: {} {}, Pressed keys: {}",
        leftShiftPressed,
        rightShiftPressed,
        leftCtrlPressed,
        rightCtrlPressed,
        leftAltPressed,
        rightAltPressed,
        pressedModifierKeyCodes);
  }

  /**
   * Processes a key event and updates the internal state of modifier keys.
   *
   * @param keyCode The virtual key code
   * @param messageType The message type (key down or key up)
   */
  void processKeyEvent(final int keyCode, final int messageType) {
    var isKeyDown = isKeyDownEvent(messageType);

    // Track modifier key codes
    if (isModifierKey(keyCode)) {
      if (isKeyDown) {
        pressedModifierKeyCodes.add(keyCode);
      } else {
        pressedModifierKeyCodes.remove(keyCode);
      }
    }

    // Update shift key states
    if (isLeftShiftKey(keyCode)) {
      updateLeftShift(isKeyDown);
    } else if (isRightShiftKey(keyCode)) {
      updateRightShift(isKeyDown);
    }

    // Update ctrl key states
    else if (isLeftCtrlKey(keyCode)) {
      updateLeftCtrl(isKeyDown);
    } else if (isRightCtrlKey(keyCode)) {
      updateRightCtrl(isKeyDown);
    }

    // Update alt key states
    else if (isLeftAltKey(keyCode)) {
      updateLeftAlt(isKeyDown);
    } else if (isRightAltKey(keyCode)) {
      updateRightAlt(isKeyDown);
    }
  }

  /**
   * Gets a copy of all currently pressed modifier key codes.
   *
   * @return a set containing the virtual key codes of all currently pressed modifier keys
   */
  Set<Integer> getPressedModifierKeyCodes() {
    return Set.copyOf(pressedModifierKeyCodes);
  }

  /**
   * Checks if a specific modifier key code is currently pressed.
   *
   * @param keyCode the virtual key code to check
   * @return true if the specified key is currently pressed, false otherwise
   */
  boolean isModifierKeyPressed(final int keyCode) {
    return pressedModifierKeyCodes.contains(keyCode);
  }

  /**
   * Checks if any modifier key (Shift, Ctrl, Alt) is pressed.
   *
   * @return true if any modifier key is pressed, false otherwise
   */
  boolean isAnyModifierPressed() {
    return isAnyShiftPressed() || isAnyCtrlPressed() || isAnyAltPressed();
  }

  boolean isKeyDownEvent(final int messageType) {
    return messageType == WinUser.WM_KEYDOWN || messageType == WinUser.WM_SYSKEYDOWN;
  }

  boolean isKeyUpEvent(final int messageType) {
    return messageType == WinUser.WM_KEYUP || messageType == WinUser.WM_SYSKEYUP;
  }

  // Shift key methods
  private void updateLeftShift(final boolean pressed) {
    leftShiftDown.set(pressed);
    LOGGER.trace("Left shift key {}", pressed ? "pressed" : "released");
  }

  private void updateRightShift(final boolean pressed) {
    rightShiftDown.set(pressed);
    LOGGER.trace("Right shift key {}", pressed ? "pressed" : "released");
  }

  boolean isLeftShiftPressed() {
    return leftShiftDown.get();
  }

  boolean isRightShiftPressed() {
    return rightShiftDown.get();
  }

  boolean isAnyShiftPressed() {
    return leftShiftDown.get() || rightShiftDown.get();
  }

  // Ctrl key methods
  private void updateLeftCtrl(final boolean pressed) {
    leftCtrlDown.set(pressed);
    LOGGER.trace("Left ctrl key {}", pressed ? "pressed" : "released");
  }

  private void updateRightCtrl(final boolean pressed) {
    rightCtrlDown.set(pressed);
    LOGGER.trace("Right ctrl key {}", pressed ? "pressed" : "released");
  }

  boolean isLeftCtrlPressed() {
    return leftCtrlDown.get();
  }

  boolean isRightCtrlPressed() {
    return rightCtrlDown.get();
  }

  boolean isAnyCtrlPressed() {
    return leftCtrlDown.get() || rightCtrlDown.get();
  }

  // Alt key methods
  private void updateLeftAlt(final boolean pressed) {
    leftAltDown.set(pressed);
    LOGGER.trace("Left alt key {}", pressed ? "pressed" : "released");
  }

  private void updateRightAlt(final boolean pressed) {
    rightAltDown.set(pressed);
    LOGGER.trace("Right alt key {}", pressed ? "pressed" : "released");
  }

  boolean isLeftAltPressed() {
    return leftAltDown.get();
  }

  boolean isRightAltPressed() {
    return rightAltDown.get();
  }

  boolean isAnyAltPressed() {
    return leftAltDown.get() || rightAltDown.get();
  }
}
