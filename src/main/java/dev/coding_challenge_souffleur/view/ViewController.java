package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.JavaFxApplication;
import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.FileService;
import dev.coding_challenge_souffleur.model.ScreenshotService;
import dev.coding_challenge_souffleur.model.StreamingAnalysisResult;
import dev.coding_challenge_souffleur.view.keylistener.Exit;
import dev.coding_challenge_souffleur.view.keylistener.MatchingModifier;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewController {

  public static final String MOCK_RESPONSE_FILE_PATH = "/prompts/mock_response.txt";
  private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);
  private static final String SHOW_PROBLEM_TEXT = "Show Problem";
  private static final String HIDE_PROBLEM_TEXT = "Hide Problem";
  private static final String ANALYSIS_COMPLETE = "Analysis complete";
  private static final String ANALYSIS_IN_PROGRESS = "Analysis in progress...";

  @FXML ScrollPane contentPane;
  @FXML FormattedTextFlow problemStatementFlow;
  @FXML FormattedTextFlow solutionDescriptionFlow;
  @FXML FormattedTextFlow solutionCodeFlow;
  @FXML FormattedTextFlow edgeCasesFlow;
  @FXML FormattedTextFlow timeComplexityFlow;
  @FXML FormattedTextFlow spaceComplexityFlow;

  private AnthropicService anthropicService;
  private ScreenshotService screenshotService;
  private PlatformRunLater platformRunLater;
  private ScreenshotDisplayService screenshotDisplayService;
  private FileService fileService;

  @FXML private Button closeButton;
  @FXML private Button toggleProblemStatementButton;
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

  void displayStreamingAnalysisResult(final StreamingAnalysisResult result) {
    ContentDisplayUtils.displayStreamingAnalysisResult(this, result, platformRunLater);

    // Update status to ensure content pane becomes visible
    var statusText = result.isComplete() ? ANALYSIS_COMPLETE : ANALYSIS_IN_PROGRESS;
    updateStatus(statusText);
  }

  void updateStatus(final String status) {
    platformRunLater.accept(
        () -> {
          LOGGER.trace("Updating status to: {}", status);
          statusLabel.setText(status);

          if (!contentPane.isVisible()) {
            contentPane.setVisible(true);
            contentPane.setManaged(true);
            ContentDisplayUtils.adjustWindowSize(this);
          }
        });
  }

  void setup(
      final AnthropicService anthropicService,
      final ScreenshotService screenshotService,
      final FileService fileService,
      final PlatformRunLater platformRunLater,
      final ScreenshotDisplayService screenshotDisplayService) {
    this.anthropicService = anthropicService;
    this.screenshotService = screenshotService;
    this.fileService = fileService;
    this.platformRunLater = platformRunLater;
    this.screenshotDisplayService = screenshotDisplayService;

    screenshotDisplayService.initialize(screenshotPreviewContainer, screenshotPreview);
  }

  public void executeScreenshotAnalysis() {
    var future = takeScreenshotAndAnalyze(this::displayStreamingAnalysisResult, this::updateStatus);
    handleAnalysisCompletion(future, "screenshot analysis");
  }

  private CompletableFuture<StreamingAnalysisResult> takeScreenshotAndAnalyze(
      final Consumer<StreamingAnalysisResult> progressCallback,
      final Consumer<String> statusCallback) {

    var optionalScreenshot = screenshotService.takeScreenshotOfDesktop();
    if (optionalScreenshot.isEmpty()) {
      statusCallback.accept("Failed to take screenshot");
      LOGGER.debug("Failed to take screenshot");
      return CompletableFuture.failedFuture(new IllegalStateException("Failed to take screenshot"));
    }

    var screenshot = optionalScreenshot.get();
    screenshotDisplayService.showScreenshotAsPreview(screenshot);

    statusCallback.accept("Sending screenshot for analysis...");
    LOGGER.trace("Coordinating screenshot analysis...");

    return anthropicService.analyseStreaming(screenshot, progressCallback);
  }

  public void executeMockAnalysis() {
    var mockResponseText =
        fileService.loadResourceFileOrDefault(
            MOCK_RESPONSE_FILE_PATH, "Error: Could not load mock response");

    updateStatus("Running mock analysis...");
    var future =
        anthropicService.analyseStreamingMock(
            mockResponseText, this::displayStreamingAnalysisResult);

    handleAnalysisCompletion(future, "mock analysis");
  }

  private void handleAnalysisCompletion(
      final CompletableFuture<StreamingAnalysisResult> future, final String analysisType) {

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
