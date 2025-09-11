package dev.coding_challenge_souffleur.view.keylistener;

import dev.coding_challenge_souffleur.view.HideShowState;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import dev.coding_challenge_souffleur.view.ViewController;
import dev.coding_challenge_souffleur.view.WindowFromScreenCaptureHider;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import javafx.stage.Stage;

record KeyCommandDependencies(
    Runnable exitApplication,
    Stage stage,
    WindowFromScreenCaptureHider windowFromScreenCaptureHider,
    HideShowState hideShowState,
    ScreenshotDisplayService screenshotDisplayService,
    ViewController viewController,
    MultiSolutionTabPane multiSolutionTabPane) {}
