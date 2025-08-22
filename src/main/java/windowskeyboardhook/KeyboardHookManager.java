package windowskeyboardhook;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Responsible for installing and uninstalling the Windows keyboard hook. */
@ApplicationScoped
class KeyboardHookManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardHookManager.class);

  private HHOOK keyboardHook;

  /**
   * Installs the keyboard hook with the provided callback procedure.
   *
   * @param proc The callback procedure to handle keyboard events
   * @return true if the hook was installed successfully, false otherwise
   */
  boolean installKeyboardHook(final LowLevelKeyboardProc proc) {
    var moduleHandle = Kernel32.INSTANCE.GetModuleHandle(null);
    keyboardHook = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, proc, moduleHandle, 0);
    if (keyboardHook == null) {
      var error = Kernel32.INSTANCE.GetLastError();
      LOGGER.warn("Failed to install Windows keyboard hook, error: {}", error);
      return false;
    }

    LOGGER.debug("Keyboard hook installed");
    return true;
  }

  /**
   * Uninstalls the keyboard hook.
   *
   * @return true if the hook was uninstalled successfully, false otherwise
   */
  boolean uninstallKeyboardHook() {
    if (keyboardHook == null) {
      LOGGER.debug("No keyboard hook to uninstall");
      return true;
    }

    var success = User32.INSTANCE.UnhookWindowsHookEx(keyboardHook);
    if (success) {
      LOGGER.debug("Keyboard hook uninstalled");
      keyboardHook = null;
      return true;
    } else {
      LOGGER.warn("Failed to properly uninstall keyboard hook");
      return false;
    }
  }

  /**
   * Checks if a keyboard hook is currently installed.
   *
   * @return true if a hook is installed, false otherwise
   */
  boolean isHookInstalled() {
    return keyboardHook != null;
  }

  /**
   * Gets the current keyboard hook.
   *
   * @return The current keyboard hook, or null if no hook is installed
   */
  HHOOK getKeyboardHook() {
    return keyboardHook;
  }
}
