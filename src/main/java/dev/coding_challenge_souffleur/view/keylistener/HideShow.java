package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.WindowFromScreenCaptureHider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
class HideShow implements KeyHandler {

  static final Win32VK HIDE_SHOW_KEY_CODE = Win32VK.VK_W;

  private final WindowFromScreenCaptureHider windowFromScreenCaptureHider;

  private final Stage stage;

  private boolean applicationIsVisible = true;

  @Inject
  HideShow(final WindowFromScreenCaptureHider windowFromScreenCaptureHider, final Stage stage) {
    this.windowFromScreenCaptureHider = windowFromScreenCaptureHider;
    this.stage = stage;
  }

  @Override
  public void performAction() {
    if (applicationIsVisible) {
      stage.hide();
    } else {
      stage.show();
      windowFromScreenCaptureHider.excludeWindowsFromScreenCapture();
    }

    applicationIsVisible = !applicationIsVisible;
  }

  @Override
  public Win32VK getKeyCode() {
    return HIDE_SHOW_KEY_CODE;
  }
}
