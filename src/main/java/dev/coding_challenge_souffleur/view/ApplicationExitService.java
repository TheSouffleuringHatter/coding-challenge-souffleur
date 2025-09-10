package dev.coding_challenge_souffleur.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ApplicationExitService {

  private final PlatformRunLater platformRunLater;
  private final ScreenshotDisplayService screenshotDisplayService;
  private final Stage stage;
  private final boolean exitPlatformOnClose;

  @Inject
  ApplicationExitService(
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService,
      final Stage stage,
      @ConfigProperty(name = "app.exit.platform.on.close") final boolean exitPlatformOnClose) {
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.stage = stage;
    this.exitPlatformOnClose = exitPlatformOnClose;
  }

  public void exitApplication() {
    platformRunLater.accept(
        () -> {
          screenshotDisplayService.stopHideTimer();
          stage.hide();

          if (exitPlatformOnClose) {
            Platform.exit();
          }
        });
  }
}
