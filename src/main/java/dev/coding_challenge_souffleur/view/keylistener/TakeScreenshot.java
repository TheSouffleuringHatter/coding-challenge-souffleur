package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class TakeScreenshot implements KeyHandler {

  private final ScreenshotDisplayService screenshotDisplayService;
  private final Win32VK screenshotKeyCode;

  @Inject
  TakeScreenshot(
      @ConfigProperty(name = "app.keyboard.key.screenshot") final Win32VK screenshotKeyCode,
      final ScreenshotDisplayService screenshotDisplayService) {
    this.screenshotDisplayService = screenshotDisplayService;
    this.screenshotKeyCode = screenshotKeyCode;
  }

  @Override
  public void performAction() {
    screenshotDisplayService.takeScreenshotOfForegroundWindowAndShowPreview();
  }

  @Override
  public Win32VK getKeyCode() {
    return screenshotKeyCode;
  }
}
