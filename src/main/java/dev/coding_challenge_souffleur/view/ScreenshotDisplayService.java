package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.ScreenshotService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Objects;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for managing screenshot preview display and auto-hide functionality. */
@ApplicationScoped
public class ScreenshotDisplayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotDisplayService.class);
  private static final Duration AUTO_HIDE_DURATION = Duration.seconds(5);

  private final PlatformRunLater platformRunLater;
  private final ScreenshotService screenshotService;
  private final PauseTransition hideScreenshotTimer = new PauseTransition(AUTO_HIDE_DURATION);

  private HBox screenshotPreviewContainer;
  private ImageView screenshotPreview;

  @Inject
  ScreenshotDisplayService(
      final PlatformRunLater platformRunLater, final ScreenshotService screenshotService) {
    this.platformRunLater = platformRunLater;
    this.screenshotService = screenshotService;
    hideScreenshotTimer.setOnFinished(event -> hideScreenshotPreviewContainer());
  }

  public void takeScreenshotAndShowPreview() {
    screenshotService.takeScreenshotOfDesktop().ifPresent(this::showScreenshotAsPreview);
  }

  public void takeScreenshotOfForegroundWindowAndShowPreview() {
    screenshotService.takeScreenshotOfForegroundWindow().ifPresent(this::showScreenshotAsPreview);
  }

  public void showScreenshotAsPreview(final Image screenshot) {
    platformRunLater.accept(
        () -> {
          hideScreenshotTimer.stop();

          var aspectRatio = screenshot.getWidth() / screenshot.getHeight();
          screenshotPreview.setFitWidth(screenshotPreview.getFitHeight() * aspectRatio);
          screenshotPreview.setImage(screenshot);

          showScreenshotPreviewContainer();
          hideScreenshotTimer.playFromStart();
        });
  }

  public void stopHideTimer() {
    hideScreenshotTimer.stop();
    LOGGER.debug("Screenshot timer stopped");
  }

  void initialize(final HBox screenshotPreviewContainer, final ImageView screenshotPreview) {
    this.screenshotPreviewContainer = Objects.requireNonNull(screenshotPreviewContainer);
    this.screenshotPreview = Objects.requireNonNull(screenshotPreview);
    LOGGER.debug("ScreenshotDisplayService initialized");
  }

  private void showScreenshotPreviewContainer() {
    screenshotPreviewContainer.setVisible(true);
    screenshotPreviewContainer.setManaged(true);
    LOGGER.debug("Show screenshot preview");
  }

  private void hideScreenshotPreviewContainer() {
    try {
      platformRunLater.accept(
          () -> {
            screenshotPreviewContainer.setVisible(false);
            screenshotPreviewContainer.setManaged(false);
            LOGGER.debug("Hided screenshot preview");
          });
    } catch (final Exception e) {
      LOGGER.debug("Timer fired after container shutdown, ignoring: {}", e.getMessage());
    }
  }
}
