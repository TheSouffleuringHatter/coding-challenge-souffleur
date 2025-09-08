package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import dev.coding_challenge_souffleur.model.ScreenshotService;
import dev.coding_challenge_souffleur.view.components.ContentPaneController;
import dev.coding_challenge_souffleur.view.components.FormattedTextFlow;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import dev.coding_challenge_souffleur.view.keylistener.Exit;
import dev.coding_challenge_souffleur.view.keylistener.MatchingModifier;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);
  private static final String ANALYSIS_COMPLETE = "Analysis complete";
  private static final String ANALYSIS_IN_PROGRESS = "Analysis in progress...";
  private AnthropicService anthropicService;
  private ScreenshotService screenshotService;
  private PlatformRunLater platformRunLater;
  private ScreenshotDisplayService screenshotDisplayService;
  private ContentPaneController contentPaneController;
  private MultiSolutionTabPane multiSolutionTabPane;
  private boolean exitPlatformOnClose;
  @FXML private VBox contentPane;
  @FXML private FormattedTextFlow problemStatementFlow;
  @FXML private Button closeButton;
  @FXML private HBox headerBox;
  @FXML private Label shortcutModifierText;
  @FXML private Label statusLabel;
  @FXML private VBox problemStatementSection;
  @FXML private HBox screenshotPreviewContainer;
  @FXML private ImageView screenshotPreview;


  @FXML
  public void initialize() {
    shortcutModifierText.setText(MatchingModifier.MATCHING_MODIFIER.toString());
    closeButton.setText("❌ (" + Character.toString(Exit.EXIT_KEY_CODE.code) + ")");
  }

  @FXML
  public void exit() {
    platformRunLater.accept(
        () -> {
          screenshotDisplayService.stopHideTimer();
          headerBox.getScene().getWindow().hide();

          if (exitPlatformOnClose) {
            Platform.exit();
          }
        });
  }

  @FXML
  public void toggleProblemStatement() {
    var isCurrentlyVisible = problemStatementSection.isVisible();

    problemStatementSection.setVisible(!isCurrentlyVisible);
    problemStatementSection.setManaged(!isCurrentlyVisible);
  }

  void updateStatus(final String status) {
    platformRunLater.accept(
        () -> {
          LOGGER.trace("Updating status to: {}", status);
          statusLabel.setText(status);

          if (!contentPane.isVisible()) {
            contentPaneController.showContentPane(contentPane);
            contentPaneController.adjustWindowSize(contentPane.getScene().getWindow());
          }
        });
  }

  void setup(
      final AnthropicService anthropicService,
      final ScreenshotService screenshotService,
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService,
      final ContentPaneController contentPaneController,
      final MultiSolutionTabPane multiSolutionTabPane,
      final boolean exitPlatformOnClose) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.contentPaneController = contentPaneController;
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.exitPlatformOnClose = exitPlatformOnClose;

    screenshotDisplayService.initialize(screenshotPreviewContainer, screenshotPreview);

    // Replace FXML TabPane with our custom MultiSolutionTabPane
    var solutionTabPane = contentPane.lookup("#solutionTabPanePlaceholder");
    var parent = (VBox) solutionTabPane.getParent();
    var index = parent.getChildren().indexOf(solutionTabPane);
    parent.getChildren().set(index, multiSolutionTabPane);
  }

  public void executeMultiSolutionAnalysis() {
    var future =
        takeScreenshotAndAnalyzeMultiSolution(this::displayMultiSolutionResult, this::updateStatus);
    handleMultiSolutionCompletion(future, "multi-solution analysis");
  }

  public void executeMultiSolutionMockAnalysis() {
    updateStatus("Running multi-solution mock analysis...");
    var future =
        anthropicService.analyseMultiSolutionMock(this::displayMultiSolutionResult);

    handleMultiSolutionCompletion(future, "multi-solution mock analysis");
  }

  void displayMultiSolutionResult(final MultiSolutionResult result) {
    platformRunLater.accept(
        () -> {
          // Use the enhanced MultiSolutionTabPane to display the result
          multiSolutionTabPane.displayResult(result);

          // Handle problem statement display using enhanced FormattedTextFlow
          var problem = result.getSharedProblemStatement();
          if (problem.isPresent()) {
            problemStatementFlow.displayProblemStatement(problem.get());
          } else {
            problemStatementFlow.displayProblemStatement(null); // Will show "Loading..."
          }

          final String statusText;
          if (result.isComplete()) {
            LOGGER.debug(
                "Multi-solution result complete, solution count: {}", result.getSolutionCount());
            statusText = ANALYSIS_COMPLETE;
          } else {
            LOGGER.trace(
                "Multi-solution result not complete, solution count: {}",
                result.getSolutionCount());
            statusText = ANALYSIS_IN_PROGRESS;
          }

          LOGGER.trace("Updating status to: {}", statusText);
          statusLabel.setText(statusText);

          if (!contentPane.isVisible()) {
            contentPaneController.showContentPane(contentPane);
            contentPaneController.adjustWindowSize(contentPane.getScene().getWindow());
          }
        });
  }

  private CompletableFuture<MultiSolutionResult> takeScreenshotAndAnalyzeMultiSolution(
      final Consumer<MultiSolutionResult> progressCallback, final Consumer<String> statusCallback) {

    var optionalScreenshot = screenshotService.takeScreenshotOfDesktop();
    if (optionalScreenshot.isEmpty()) {
      statusCallback.accept("Failed to take screenshot");
      LOGGER.debug("Failed to take screenshot");
      return CompletableFuture.failedFuture(new IllegalStateException("Failed to take screenshot"));
    }

    var screenshot = optionalScreenshot.get();
    screenshotDisplayService.showScreenshotAsPreview(screenshot);

    statusCallback.accept("Sending screenshot for multi-solution analysis...");
    LOGGER.trace("Coordinating multi-solution analysis...");

    return anthropicService.analyseMultiSolution(screenshot, progressCallback);
  }

  private void handleMultiSolutionCompletion(
      final CompletableFuture<MultiSolutionResult> future, final String analysisType) {

    LOGGER.trace("Starting {}...", analysisType);

    future.whenComplete(
        (finalResult, error) -> {
          if (error == null) {
            LOGGER.debug("Analysis '{}' completed successfully", analysisType);
            updateStatus(ANALYSIS_COMPLETE);
          } else {
            LOGGER.warn("Error in {}", analysisType, error);
            updateStatus("Error in " + analysisType + ": " + error.getMessage());
          }
        });
  }
}
