package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import java.util.Optional;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class for displaying streaming analysis results in the UI. */
final class ContentDisplayUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentDisplayUtils.class);
  private static final double WINDOW_WIDTH_RATIO = 0.3;

  private ContentDisplayUtils() {
    // Utility class
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

  static void displayMultiSolutionResult(
      final ViewController viewController,
      final MultiSolutionResult result,
      final PlatformRunLater platformRunLater) {
    LOGGER.trace("Displaying multi-solution result...");

    platformRunLater.accept(
        () -> {
          showContentPaneIfNotVisible(viewController);

          // Clear existing tabs
          viewController.solutionTabPane.getTabs().clear();

          // Ensure we have tabs for all solutions
          var solutionCount = Math.max(1, result.getSolutionCount());
          SolutionTabManager.ensureTabCount(viewController.solutionTabPane, solutionCount);

          // Update each solution tab
          for (int i = 0; i < solutionCount; i++) {
            var tab = viewController.solutionTabPane.getTabs().get(i);
            var solutionOpt = result.getSolution(i);
            
            if (solutionOpt.isPresent()) {
              var solution = solutionOpt.get();
              
              // Set tab title from solution title or default
              var tabTitle = solution.getSolutionTitle().orElse("Solution " + (i + 1));
              tab.setText(tabTitle);
              
              // Get the tab data and update the content
              var tabData = SolutionTabManager.getTabData(tab);
              updateSection(tabData.getSolutionDescriptionFlow(), solution.getSolutionDescription(), false);
              updateSection(tabData.getEdgeCasesFlow(), solution.getEdgeCases(), false);
              updateSection(tabData.getSolutionCodeFlow(), solution.getSolutionCode(), true);
              updateSection(tabData.getTimeComplexityFlow(), solution.getTimeComplexity(), false);
              updateSection(tabData.getSpaceComplexityFlow(), solution.getSpaceComplexity(), false);
            } else {
              // Empty solution - show loading
              tab.setText("Solution " + (i + 1));
              var tabData = SolutionTabManager.getTabData(tab);
              updateSection(tabData.getSolutionDescriptionFlow(), Optional.of("Loading..."), false);
              updateSection(tabData.getEdgeCasesFlow(), Optional.empty(), false);
              updateSection(tabData.getSolutionCodeFlow(), Optional.empty(), true);
              updateSection(tabData.getTimeComplexityFlow(), Optional.empty(), false);
              updateSection(tabData.getSpaceComplexityFlow(), Optional.empty(), false);
            }
          }

          // Update shared problem statement
          updateSection(viewController.problemStatementFlow, result.getSharedProblemStatement(), false);

          adjustWindowSize(viewController);
        });
  }
}
