package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.JavaFxApplication;
import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.FileService;
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
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewController {

  public static final String MULTI_SOLUTION_MOCK_RESPONSE_FILE_PATH =
      "/prompts/multi_solution_mock.txt";

  private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);
  private static final String SHOW_PROBLEM_TEXT = "Show Problem";
  private static final String HIDE_PROBLEM_TEXT = "Hide Problem";
  private static final String ANALYSIS_COMPLETE = "Analysis complete";
  private static final String ANALYSIS_IN_PROGRESS = "Analysis in progress...";
  @FXML public TabPane solutionTabPane;
  @FXML VBox contentPane;
  @FXML FormattedTextFlow problemStatementFlow;
  private AnthropicService anthropicService;
  private ScreenshotService screenshotService;
  private PlatformRunLater platformRunLater;
  private ScreenshotDisplayService screenshotDisplayService;
  private FileService fileService;
  private ContentPaneController contentPaneController;
  private MultiSolutionTabPane multiSolutionTabPane;
  @FXML private Button closeButton;
  @FXML private Button toggleProblemStatementButton;
  @FXML private HBox headerBox;
  @FXML private Label shortcutModifierText;
  @FXML private Label statusLabel;
  @FXML private VBox problemStatementSection;
  @FXML private HBox screenshotPreviewContainer;
  @FXML private ImageView screenshotPreview;

  // Expose the active MultiSolutionTabPane for key handlers
  public MultiSolutionTabPane getActiveTabPane() {
    return multiSolutionTabPane;
  }

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

          if (!Boolean.getBoolean(JavaFxApplication.APPLICATION_TESTING_FLAG)) {
            Platform.exit();
          }
        });
  }

  @FXML
  public void toggleProblemStatement() {
    var isCurrentlyVisible = problemStatementSection.isVisible();

    problemStatementSection.setVisible(!isCurrentlyVisible);
    problemStatementSection.setManaged(!isCurrentlyVisible);

    toggleProblemStatementButton.setText(
        isCurrentlyVisible ? SHOW_PROBLEM_TEXT : HIDE_PROBLEM_TEXT);
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
      final FileService fileService,
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService,
      final ContentPaneController contentPaneController,
      final MultiSolutionTabPane multiSolutionTabPane) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.fileService = fileService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;
    this.contentPaneController = contentPaneController;
    this.multiSolutionTabPane = multiSolutionTabPane;

    screenshotDisplayService.initialize(screenshotPreviewContainer, screenshotPreview);

    // Replace FXML TabPane with our custom MultiSolutionTabPane
    var parent = solutionTabPane.getParent();
    if (parent instanceof final VBox vbox) {
      var index = vbox.getChildren().indexOf(solutionTabPane);
      vbox.getChildren().set(index, multiSolutionTabPane);
    }
  }

  public void executeMultiSolutionAnalysis() {
    var future =
        takeScreenshotAndAnalyzeMultiSolution(this::displayMultiSolutionResult, this::updateStatus);
    handleMultiSolutionCompletion(future, "multi-solution analysis");
  }

  public void executeMultiSolutionMockAnalysis() {
    var mockResponseText =
        fileService.loadResourceFileOrDefault(
            MULTI_SOLUTION_MOCK_RESPONSE_FILE_PATH,
            "Error: Could not load multi-solution mock response");

    updateStatus("Running multi-solution mock analysis...");
    var future =
        anthropicService.analyseMultiSolutionMock(
            mockResponseText, this::displayMultiSolutionResult);

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
          if (error != null) {
            LOGGER.warn("Error in {}", analysisType, error);
            updateStatus("Error in " + analysisType + ": " + error.getMessage());
          } else {
            LOGGER.debug("Analysis '{}' completed successfully", analysisType);
          }
        });
  }
}
