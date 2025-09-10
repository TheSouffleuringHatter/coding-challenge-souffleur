package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.WindowFromScreenCaptureHider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class HideShow implements KeyHandler {

  private final WindowFromScreenCaptureHider windowFromScreenCaptureHider;
  private final Stage stage;
  private final Win32VK hideShowKeyCode;

  private boolean applicationIsVisible = true;

  @Inject
  HideShow(
      @ConfigProperty(name = "app.keyboard.key.hide_show") final Win32VK hideShowKeyCode,
      final WindowFromScreenCaptureHider windowFromScreenCaptureHider,
      final Stage stage) {
    this.windowFromScreenCaptureHider = windowFromScreenCaptureHider;
    this.stage = stage;
    this.hideShowKeyCode = hideShowKeyCode;
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
    return hideShowKeyCode;
  }
}
