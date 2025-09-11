package dev.coding_challenge_souffleur.view.keylistener;

import dev.coding_challenge_souffleur.view.HideShowState;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import dev.coding_challenge_souffleur.view.ViewController;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import javafx.stage.Stage;

@ApplicationScoped
class KeyCommandDependencies {

  private final Runnable exitApplication;
  private final Stage stage;
  private final HideShowState hideShowState;
  private final ScreenshotDisplayService screenshotDisplayService;
  private final ViewController viewController;
  private final MultiSolutionTabPane multiSolutionTabPane;

  @Inject
  KeyCommandDependencies(
      @Named("exitApplication") final Runnable exitApplication,
      final Stage stage,
      final HideShowState hideShowState,
      final ScreenshotDisplayService screenshotDisplayService,
      final ViewController viewController,
      final MultiSolutionTabPane multiSolutionTabPane) {
    this.exitApplication = exitApplication;
    this.stage = stage;
    this.hideShowState = hideShowState;
    this.screenshotDisplayService = screenshotDisplayService;
    this.viewController = viewController;
    this.multiSolutionTabPane = multiSolutionTabPane;
  }

  public Runnable exitApplication() {
    return exitApplication;
  }

  public Stage stage() {
    return stage;
  }

  public HideShowState hideShowState() {
    return hideShowState;
  }

  public ScreenshotDisplayService screenshotDisplayService() {
    return screenshotDisplayService;
  }

  public ViewController viewController() {
    return viewController;
  }

  public MultiSolutionTabPane multiSolutionTabPane() {
    return multiSolutionTabPane;
  }
}
