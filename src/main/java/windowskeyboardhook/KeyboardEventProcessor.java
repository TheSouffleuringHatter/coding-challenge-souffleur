package windowskeyboardhook;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.coding_challenge_souffleur.ConfigurationKeys;

/** Responsible for processing keyboard events and notifying listeners. */
@ApplicationScoped
class KeyboardEventProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardEventProcessor.class);

  private static final int LLKHF_INJECTED = 0x10;
  private static final int LLKHF_LOWER_IL_INJECTED = 0x02;
  private static final int INJECTED_FLAGS_MASK = LLKHF_INJECTED | LLKHF_LOWER_IL_INJECTED;

  private final List<WindowsKeyListener> keyListeners;
  private final KeyboardStateManager keyboardStateManager;
  private final KeyboardHookManager keyboardHookManager;
  private final boolean filterInjectedKeys;

  @Inject
  KeyboardEventProcessor(
      final KeyboardStateManager keyboardStateManager,
      final KeyboardHookManager keyboardHookManager,
      @Any final Instance<WindowsKeyListener> keyListenerInstances,
      @ConfigProperty(name = ConfigurationKeys.APP_KEYBOARD_FILTER_INJECTED_KEYS)
          final boolean filterInjectedKeys) {
    this.keyboardStateManager = keyboardStateManager;
    this.keyboardHookManager = keyboardHookManager;
    this.filterInjectedKeys = filterInjectedKeys;

    this.keyListeners = new ArrayList<>();
    keyListenerInstances.forEach(keyListeners::add);
    LOGGER.debug(
        "Initialized with {} key listeners, filter injected keys: {}",
        keyListeners.size(),
        filterInjectedKeys);
  }

  /**
   * Handles a keyboard event.
   *
   * @param nCode The hook code
   * @param wParam The message type
   * @param info The keyboard event information
   * @return The result of the event processing
   */
  LRESULT handleKeyboardEvent(final int nCode, final WPARAM wParam, final KBDLLHOOKSTRUCT info) {
    LOGGER.trace("Keyboard event received: nCode={}, wParam={}", nCode, wParam.intValue());

    if (nCode < 0) {
      return callNextHook(nCode, wParam, info);
    }

    // Ignore injected keystrokes to avoid feedback loops and incompatibilities with other tools
    var flags = info.flags;
    if (filterInjectedKeys && (flags & INJECTED_FLAGS_MASK) != 0) {
      LOGGER.debug("Ignoring injected keystroke: flags=0x{}", Integer.toHexString(flags));
      return callNextHook(nCode, wParam, info);
    }

    var virtualKeyCode = info.vkCode;
    var messageType = wParam.intValue();

    keyboardStateManager.processKeyEvent(virtualKeyCode, messageType);

    var event = createKeyEvent(Win32VK.fromValue(virtualKeyCode), messageType);
    var eventConsumed =
        keyListeners.stream().filter(l -> l.responsibleFor(event)).anyMatch(l -> l.consume(event));

    if (eventConsumed) {
      LOGGER.trace("Event consumed, preventing further propagation");
      return new LRESULT(1); // Non-zero return value consumes the event
    }

    // If the event was not consumed, pass it to the next hook
    return callNextHook(nCode, wParam, info);
  }

  /**
   * Creates a Windows key event from the given parameters. Includes information about all modifier
   * keys (Shift, Ctrl, Alt).
   *
   * @param virtualKeyCode The virtual key code
   * @param messageType The message type
   * @return The created key event
   */
  private WindowsKeyEvent createKeyEvent(final Win32VK virtualKeyCode, final int messageType) {
    LOGGER.trace("Processing key event with vkCode={}", virtualKeyCode);

    return new WindowsKeyEvent(
        virtualKeyCode,
        keyboardStateManager.getPressedModifierKeyCodes(),
        keyboardStateManager.isAnyShiftPressed(),
        keyboardStateManager.isLeftShiftPressed(),
        keyboardStateManager.isRightShiftPressed(),
        keyboardStateManager.isAnyCtrlPressed(),
        keyboardStateManager.isLeftCtrlPressed(),
        keyboardStateManager.isRightCtrlPressed(),
        keyboardStateManager.isAnyAltPressed(),
        keyboardStateManager.isLeftAltPressed(),
        keyboardStateManager.isRightAltPressed(),
        keyboardStateManager.isKeyDownEvent(messageType),
        keyboardStateManager.isKeyUpEvent(messageType));
  }

  /**
   * Calls the next hook in the chain.
   *
   * @param nCode The hook code
   * @param wParam The message type
   * @param info The keyboard event information
   * @return The result of the next hook
   */
  private LRESULT callNextHook(final int nCode, final WPARAM wParam, final KBDLLHOOKSTRUCT info) {
    return User32.INSTANCE.CallNextHookEx(
        keyboardHookManager.getKeyboardHook(),
        nCode,
        wParam,
        new LPARAM(Pointer.nativeValue(info.getPointer())));
  }
}
