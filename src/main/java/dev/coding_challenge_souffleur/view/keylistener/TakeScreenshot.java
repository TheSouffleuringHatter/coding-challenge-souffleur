package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class TakeScreenshot implements KeyHandler {

  static final Win32VK SCREENSHOT_KEY_CODE = Win32VK.VK_R;

  private final ScreenshotDisplayService screenshotDisplayService;

  @Inject
  TakeScreenshot(final ScreenshotDisplayService screenshotDisplayService) {
    this.screenshotDisplayService = screenshotDisplayService;
  }

  @Override
  public void performAction() {
    screenshotDisplayService.takeScreenshotOfForegroundWindowAndShowPreview();
  }

  @Override
  public Win32VK getKeyCode() {
    return SCREENSHOT_KEY_CODE;
  }
}
