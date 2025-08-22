package dev.coding_challenge_souffleur.view;

import com.sun.glass.ui.Window;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APIOptions;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for preventing windows from appearing in screen captures. This class uses Windows API to set
 * display affinity flags on JavaFX windows.
 */
@ApplicationScoped
public class WindowFromScreenCaptureHider {

  private static final Logger LOGGER = LoggerFactory.getLogger(WindowFromScreenCaptureHider.class);

  /**
   * Applies screen capture protection to a specific window.
   *
   * @param window The JavaFX window to protect
   * @return true if protection was successfully applied, false otherwise
   */
  private static boolean protectWindowFromScreenCapture(final Window window) {
    if (!window.isVisible()) {
      return false;
    }

    var hwnd = new HWND(Pointer.createConstant(window.getNativeWindow()));
    var success = WinUserLibrary.INSTANCE.excludeWindowFromScreenCapture(hwnd);
    if (!success) {
      LOGGER.warn("Failed to protect window from screen capture");
    }

    return success;
  }

  /**
   * Configures all visible JavaFX windows to be excluded from screen captures. This method must be
   * called after windows are rendered and visible.
   */
  public void excludeWindowsFromScreenCapture() {
    if (Boolean.getBoolean("testfx.headless")) {
      LOGGER.debug("Running in headless mode, skipping screen capture protection");
      return;
    }

    var anyWindowProtected = false;
    for (final var window : Window.getWindows()) {
      if (protectWindowFromScreenCapture(window)) {
        anyWindowProtected = true;
      }
    }

    if (!anyWindowProtected) {
      LOGGER.warn("No windows were protected from screen capture");
    }
  }

  private interface WinUserLibrary extends Library {

    WinUserLibrary INSTANCE =
        Native.load("user32", WinUserLibrary.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Windows API flag to exclude windows from screen capture.
     *
     * @see <a
     *     href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowdisplayaffinity#parameters">
     *     Windows Documentation on SetWindowDisplayAffinity</a>
     */
    int WDA_EXCLUDEFROMCAPTURE = 0x00000011;

    /**
     * Sets the display affinity for a window.
     *
     * @param hWnd Window handle
     * @param dwAffinity Desired display affinity flag
     * @return true if successful, false otherwise
     */
    boolean SetWindowDisplayAffinity(final HWND hWnd, final int dwAffinity);

    /**
     * Excludes a window from screen capture.
     *
     * @param hWnd Window handle to exclude from screen capture
     * @return true if successful, false otherwise
     */
    default boolean excludeWindowFromScreenCapture(final HWND hWnd) {
      return SetWindowDisplayAffinity(hWnd, WDA_EXCLUDEFROMCAPTURE);
    }
  }
}
