package dev.coding_challenge_souffleur.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
public class HideShowState {

  private final WindowFromScreenCaptureHider windowFromScreenCaptureHider;
  private boolean applicationVisible = true;

  @Inject
  HideShowState(WindowFromScreenCaptureHider windowFromScreenCaptureHider) {
    this.windowFromScreenCaptureHider = windowFromScreenCaptureHider;
  }

  public void toggleVisibility(Stage stage) {
    if (applicationVisible) {
      stage.hide();
    } else {
      stage.show();
      windowFromScreenCaptureHider.excludeWindowsFromScreenCapture();
    }
    applicationVisible = !applicationVisible;
  }
}
