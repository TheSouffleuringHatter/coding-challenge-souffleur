package dev.coding_challenge_souffleur.view;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.ScreenshotService;
import dev.coding_challenge_souffleur.view.components.ContentPaneController;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import dev.coding_challenge_souffleur.view.keylistener.ShortcutKeysLabel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class MainSceneCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainSceneCreator.class);

  private static final String VIEW_FXML_RESOURCE = "view.fxml";

  private final AnthropicService anthropicService;
  private final ScreenshotService screenshotService;
  private final PlatformRunLater platformRunLater;
  private final ScreenshotDisplayService screenshotDisplayService;
  private final ContentPaneController contentPaneController;
  private final Instance<Stage> stageInstance;
  private final boolean exitPlatformOnClose;
  private final Win32VK exitKeyCode;
  private final Win32VK hideShowKey;
  private final Win32VK moveUpKey;
  private final Win32VK moveDownKey;
  private final Win32VK moveLeftKey;
  private final Win32VK moveRightKey;
  private final Win32VK screenshotKey;
  private final Win32VK runAnalysisKey;
  private final Win32VK scrollUpKey;
  private final Win32VK scrollDownKey;

  @Produces private ViewController viewController;
  @Produces private Scene mainScene;
  @Produces private MultiSolutionTabPane multiSolutionTabPane;

  @Inject
  MainSceneCreator(
      final AnthropicService anthropicService,
      final ScreenshotService screenshotService,
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService,
      final ContentPaneController contentPaneController,
      final Instance<Stage> stageInstance,
      @ConfigProperty(name = "app.exit.platform.on.close") final boolean exitPlatformOnClose,
      @ConfigProperty(name = "app.keyboard.key.exit") final Win32VK exitKeyCode,
      @ConfigProperty(name = "app.keyboard.key.hide_show") final Win32VK hideShowKey,
      @ConfigProperty(name = "app.keyboard.key.move_up") final Win32VK moveUpKey,
      @ConfigProperty(name = "app.keyboard.key.move_down") final Win32VK moveDownKey,
      @ConfigProperty(name = "app.keyboard.key.move_left") final Win32VK moveLeftKey,
      @ConfigProperty(name = "app.keyboard.key.move_right") final Win32VK moveRightKey,
      @ConfigProperty(name = "app.keyboard.key.screenshot") final Win32VK screenshotKey,
      @ConfigProperty(name = "app.keyboard.key.run_analysis") final Win32VK runAnalysisKey,
      @ConfigProperty(name = "app.keyboard.key.scroll_up") final Win32VK scrollUpKey,
      @ConfigProperty(name = "app.keyboard.key.scroll_down") final Win32VK scrollDownKey) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.contentPaneController = contentPaneController;
    this.stageInstance = stageInstance;
    this.exitPlatformOnClose = exitPlatformOnClose;
    this.exitKeyCode = exitKeyCode;
    this.hideShowKey = hideShowKey;
    this.moveUpKey = moveUpKey;
    this.moveDownKey = moveDownKey;
    this.moveLeftKey = moveLeftKey;
    this.moveRightKey = moveRightKey;
    this.screenshotKey = screenshotKey;
    this.runAnalysisKey = runAnalysisKey;
    this.scrollUpKey = scrollUpKey;
    this.scrollDownKey = scrollDownKey;
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
    this.multiSolutionTabPane = new MultiSolutionTabPane();
    this.viewController.setup(
        anthropicService,
        screenshotService,
        platformRunLater,
        screenshotDisplayService,
        contentPaneController,
        multiSolutionTabPane,
        exitKeyCode);
    setupShortcutKeysLabel();

    LOGGER.debug("Main scene created from {}", VIEW_FXML_RESOURCE);
  }

  @Produces
  @Named("exitApplication")
  Runnable createExitApplicationAction() {
    return () ->
        platformRunLater.accept(
            () -> {
              screenshotDisplayService.stopHideTimer();
              stageInstance.get().hide();

              if (exitPlatformOnClose) {
                Platform.exit();
              }
            });
  }

  private void setupShortcutKeysLabel() {
    var shortcutKeysLabel = (ShortcutKeysLabel) mainScene.lookup("ShortcutKeysLabel");
    Objects.requireNonNull(shortcutKeysLabel, "ShortcutKeysLabel not found in scene");

    var initializedLabel =
      new ShortcutKeysLabel(
        hideShowKey,
        moveUpKey,
        moveDownKey,
        moveLeftKey,
        moveRightKey,
        screenshotKey,
        runAnalysisKey,
        scrollUpKey,
        scrollDownKey);
    shortcutKeysLabel.setText(initializedLabel.getText());
  }
}
