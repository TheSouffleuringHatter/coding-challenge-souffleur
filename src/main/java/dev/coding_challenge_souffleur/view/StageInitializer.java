package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.JavaFxApplication;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces JavaFX Stage instances for the application. This class centralizes the creation and
 * configuration of stages.
 */
@ApplicationScoped
public class StageInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(StageInitializer.class);

  private final WindowFromScreenCaptureHider windowFromScreenCaptureHider;

  private final Scene mainScene;

  private Stage stage;

  @Inject
  StageInitializer(
      final WindowFromScreenCaptureHider windowFromScreenCaptureHider, final Scene mainScene) {
    this.mainScene = mainScene;
    this.windowFromScreenCaptureHider = windowFromScreenCaptureHider;
  }

  /**
   * Creates a hidden utility stage that owns the main visible stage. This prevents the application
   * from appearing in the taskbar.
   *
   * @return A configured utility stage
   */
  private static Stage createAndShowUtilityStage() {
    var utilityStage = new Stage();
    utilityStage.initStyle(StageStyle.UTILITY);

    // Create a minimal scene and move off-screen
    var utilityScene = new Scene(new StackPane());
    utilityStage.setScene(utilityScene);
    utilityStage.setWidth(1);
    utilityStage.setHeight(1);
    utilityStage.setOpacity(0);
    utilityStage.setX(-100);
    utilityStage.setY(-100);
    utilityStage.show();

    return utilityStage;
  }

  @PostConstruct
  void createStage() {
    LOGGER.trace("Creating stage...");

    Platform.setImplicitExit(false);

    // In testing mode, JavaFX Platform is already initialized by TestFX,
    // so we can create the stage synchronously
    if (Boolean.getBoolean(JavaFxApplication.APPLICATION_TESTING_FLAG)) {
      try {
        this.createAndShowOverlayStage();
        windowFromScreenCaptureHider.excludeWindowsFromScreenCapture();
        LOGGER.debug("Stage initialization complete (testing mode)");
      } catch (Exception e) {
        throw new RuntimeException("Failed to initialize stage in testing mode", e);
      }
      return;
    }

    // In production mode, stage creation is deferred until getStage() is called
    // This avoids the timing issue where @PostConstruct runs before JavaFX Platform is ready
    LOGGER.debug("Stage creation deferred until first access");
  }

  @Produces
  public Stage getStage() {
    // In testing mode, stage was created in @PostConstruct
    if (Boolean.getBoolean(JavaFxApplication.APPLICATION_TESTING_FLAG)) {
      return stage;
    }

    // In production mode, create stage on first access
    if (stage == null) {
      synchronized (this) {
        if (stage == null) {
          try {
            this.createAndShowOverlayStage();
            windowFromScreenCaptureHider.excludeWindowsFromScreenCapture();
            LOGGER.debug("Stage initialization complete (production mode)");
          } catch (Exception e) {
            throw new RuntimeException("Failed to initialize stage in production mode", e);
          }
        }
      }
    }

    return stage;
  }

  /** Creates a transparent overlay stage owned by the utility stage. */
  private void createAndShowOverlayStage() {
    var utilityStage = createAndShowUtilityStage();

    var overlayStage = new Stage();
    overlayStage.initOwner(utilityStage);
    overlayStage.initStyle(StageStyle.TRANSPARENT);
    overlayStage.setOpacity(0.8);
    overlayStage.setAlwaysOnTop(true);
    overlayStage.setScene(this.mainScene);
    overlayStage.show();

    this.stage = overlayStage;
  }
}
