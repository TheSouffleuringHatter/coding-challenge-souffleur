package dev.coding_challenge_souffleur.view;

import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import dev.coding_challenge_souffleur.model.SolutionSection;
import javafx.scene.control.Tab;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ContentDisplayUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentDisplayUtils.class);
  private static final double WINDOW_WIDTH_RATIO = 0.3;

  private ContentDisplayUtils() {
    // Utility class
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

          // Ensure we have tabs for all solutions and build them directly
          var solutionCount = Math.max(1, result.getSolutionCount());

          for (var i = 0; i < solutionCount; i++) {
            var solutionOpt = result.getSolution(i);

            var content = new SolutionTabContent();
            var tab = new Tab();
            tab.setContent(content);

            if (solutionOpt.isPresent()) {
              var solution = solutionOpt.get();

              // Set tab title from solution title or default
              var tabTitle = "Solution " + (i + 1);
              if (solution.getSection(SolutionSection.SOLUTION_TITLE).isPresent()) {
                tabTitle =
                    "("
                        + (i + 1)
                        + ") "
                        + solution.getSection(SolutionSection.SOLUTION_TITLE).get();
              }
              tab.setText(tabTitle);

              content.setSolutionDescription(
                  solution.getSection(SolutionSection.SOLUTION_DESCRIPTION).orElse(null));
              content.setEdgeCases(
                  solution.getSection(SolutionSection.EDGE_CASES).orElse(null));
              content.setSolutionCode(
                  solution.getSection(SolutionSection.SOLUTION_CODE).orElse(null));
              content.setTimeComplexity(
                  solution.getSection(SolutionSection.TIME_COMPLEXITY).orElse(null));
              content.setSpaceComplexity(
                  solution.getSection(SolutionSection.SPACE_COMPLEXITY).orElse(null));
            } else {
              // Empty solution - rely on FXML defaults
              tab.setText("Solution " + (i + 1));
            }

            viewController.solutionTabPane.getTabs().add(tab);
          }

          // Problem statement: set if present, otherwise leave blank
          var problem = result.getSharedProblemStatement();
          if (problem.isPresent()) {
            viewController.problemStatementFlow.setFormattedContent(problem.get());
          } else {
            viewController.problemStatementFlow.setFormattedContent("Loading...");
          }

          adjustWindowSize(viewController);
        });
  }
}
