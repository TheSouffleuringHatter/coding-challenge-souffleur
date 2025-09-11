package dev.coding_challenge_souffleur.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
public class HideShowState {

  private final WindowFromScreenCaptureHider windowFromScreenCaptureHider;
  private final Stage stage;
  private boolean applicationVisible = true;

  @Inject
  HideShowState(
      final Stage stage, final WindowFromScreenCaptureHider windowFromScreenCaptureHider) {
    this.stage = stage;
    this.windowFromScreenCaptureHider = windowFromScreenCaptureHider;
  }

  public void toggleVisibility() {
    if (applicationVisible) {
      stage.hide();
    } else {
      stage.show();
      windowFromScreenCaptureHider.excludeWindowsFromScreenCapture();
    }
    applicationVisible = !applicationVisible;
  }
}
