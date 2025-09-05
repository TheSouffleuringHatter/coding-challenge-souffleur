package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.FileService;
import dev.coding_challenge_souffleur.model.ScreenshotService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class MainSceneCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainSceneCreator.class);

  private static final String VIEW_FXML_RESOURCE = "view.fxml";

  private final AnthropicService anthropicService;
  private final ScreenshotService screenshotService;
  private final FileService fileService;
  private final PlatformRunLater platformRunLater;
  private final ScreenshotDisplayService screenshotDisplayService;
  private final ContentDisplayUtils contentDisplayUtils;

  @Produces private ViewController viewController;
  @Produces private Scene mainScene;
  @Produces private VBox contentPane;

  @Inject
  MainSceneCreator(
      final AnthropicService anthropicService,
      final ScreenshotService screenshotService,
      final FileService fileService,
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService,
      final ContentDisplayUtils contentDisplayUtils) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.fileService = fileService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.contentDisplayUtils = contentDisplayUtils;
  }

  @PostConstruct
  void createMainScene() {
    var viewFxmlPath = ViewController.class.getResource(VIEW_FXML_RESOURCE);
    var fxmlLoader = new FXMLLoader(viewFxmlPath);

    Scene scene;
    try {
      scene = new Scene(fxmlLoader.load());
      scene.setFill(Color.TRANSPARENT);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    this.mainScene = scene;

    this.viewController = fxmlLoader.getController();
    this.viewController.setup(
        anthropicService,
        screenshotService,
        fileService,
        platformRunLater,
        screenshotDisplayService,
        contentDisplayUtils);
    this.contentPane = this.viewController.contentPane;

    LOGGER.trace("Main scene created from {}", VIEW_FXML_RESOURCE);
  }
}
