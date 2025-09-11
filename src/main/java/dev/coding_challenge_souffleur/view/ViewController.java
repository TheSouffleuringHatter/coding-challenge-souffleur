package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import dev.coding_challenge_souffleur.model.ScreenshotService;
import dev.coding_challenge_souffleur.view.components.ContentPaneController;
import dev.coding_challenge_souffleur.view.components.FormattedTextFlow;
import dev.coding_challenge_souffleur.view.components.HeaderBox;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.fxml.FXML;
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
  private HeaderBox headerBox;
  @FXML private VBox contentPane;
  @FXML private FormattedTextFlow problemStatementFlow;
  @FXML private HBox headerBoxPlaceholder;
  @FXML private Label statusLabel;
  @FXML private VBox problemStatementSection;
  @FXML private HBox screenshotPreviewContainer;
  @FXML private ImageView screenshotPreview;

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
      final HeaderBox headerBox) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.contentPaneController = contentPaneController;
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.headerBox = headerBox;

    screenshotDisplayService.initialize(screenshotPreviewContainer, screenshotPreview);

    // Replace FXML TabPane with our custom MultiSolutionTabPane
    var solutionTabPane = contentPane.lookup("#solutionTabPanePlaceholder");
    var parent = (VBox) solutionTabPane.getParent();
    var index = parent.getChildren().indexOf(solutionTabPane);
    parent.getChildren().set(index, multiSolutionTabPane);

    // Replace FXML HBox placeholder with our custom HeaderBox
    headerBox.setId("headerBox"); // Set ID for test compatibility
    var mainContainer = (VBox) headerBoxPlaceholder.getParent();
    var headerIndex = mainContainer.getChildren().indexOf(headerBoxPlaceholder);
    mainContainer.getChildren().set(headerIndex, headerBox);
  }

  public HeaderBox getHeaderBox() {
    return headerBox;
  }

  public void executeMultiSolutionAnalysis() {
    var future =
        takeScreenshotAndAnalyzeMultiSolution(this::displayMultiSolutionResult, this::updateStatus);
    handleMultiSolutionCompletion(future, "multi-solution analysis");
  }

  public void executeMultiSolutionMockAnalysis() {
    updateStatus("Running multi-solution mock analysis...");
    var future = anthropicService.analyseMultiSolutionMock(this::displayMultiSolutionResult);

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
      var failureMessage = "Failed to take screenshot";
      statusCallback.accept(failureMessage);
      LOGGER.debug(failureMessage);
      return CompletableFuture.failedFuture(new IllegalStateException(failureMessage));
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
