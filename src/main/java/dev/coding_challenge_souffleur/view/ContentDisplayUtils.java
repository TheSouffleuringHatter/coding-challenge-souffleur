package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.StreamingAnalysisResult;
import java.util.Optional;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class for displaying streaming analysis results in the UI. */
public final class ContentDisplayUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentDisplayUtils.class);
  private static final double WINDOW_WIDTH_RATIO = 0.3;

  private ContentDisplayUtils() {
    // Utility class
  }

  static void displayStreamingAnalysisResult(
      final ViewController viewController,
      final StreamingAnalysisResult result,
      final PlatformRunLater platformRunLater) {
    LOGGER.trace("Displaying streaming analysis result...");

    platformRunLater.accept(
        () -> {
          showContentPaneIfNotVisible(viewController);

          updateSection(viewController.solutionCodeFlow, result.getSolutionCode(), true);
          updateSection(
              viewController.solutionDescriptionFlow, result.getSolutionDescription(), false);
          updateSection(viewController.edgeCasesFlow, result.getEdgeCases(), false);
          updateSection(viewController.timeComplexityFlow, result.getTimeComplexity(), false);
          updateSection(viewController.spaceComplexityFlow, result.getSpaceComplexity(), false);
          updateSection(viewController.problemStatementFlow, result.getProblemStatement(), false);

          adjustWindowSize(viewController);
        });
  }

  private static void updateSection(
      final FormattedTextFlow textFlow,
      final Optional<String> optionalContent,
      final boolean isCode) {
    if (optionalContent.isPresent()) {
      textFlow.setFormattedContent(optionalContent.get(), isCode);
    } else {
      textFlow.setFormattedContent("Loading...", false);
    }
  }

  static void adjustWindowSize(final ViewController viewController) {
    var scene = viewController.contentPane.getScene();
    if (scene != null && scene.getWindow() != null) {
      adjustWindowToScreen(scene.getWindow());
      LOGGER.trace("Window size adjusted");
    }
  }

  private static void adjustWindowToScreen(final javafx.stage.Window window) {
    var screenBounds = Screen.getPrimary().getVisualBounds();
    window.setWidth(screenBounds.getWidth() * WINDOW_WIDTH_RATIO);
    window.setHeight(screenBounds.getHeight());
    window.setY(screenBounds.getMinY());
  }

  private static void showContentPaneIfNotVisible(final ViewController viewController) {
    LOGGER.trace("Content pane visible: {}", viewController.contentPane.isVisible());
    if (!viewController.contentPane.isVisible()) {
      LOGGER.trace("Making content pane visible...");
      viewController.contentPane.setVisible(true);
      viewController.contentPane.setManaged(true);
      LOGGER.debug("Made content pane visible");
    }
  }
}
