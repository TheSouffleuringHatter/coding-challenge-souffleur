package dev.coding_challenge_souffleur.view.components;

import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom JavaFX component extending TabPane that handles the display of multiple solutions.
 * This component understands domain objects and manages its own display logic.
 * Note: This is not a CDI-managed bean because JavaFX components cannot be proxied.
 */
public class MultiSolutionTabPane extends TabPane {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiSolutionTabPane.class);

  private final PlatformRunLater platformRunLater;

  public MultiSolutionTabPane(final PlatformRunLater platformRunLater) {
    this.platformRunLater = platformRunLater;
    this.getStyleClass().add("solution-tabs");
    this.setId("solutionTabPane");
  }

  /**
   * Displays the given MultiSolutionResult by creating and managing solution tabs internally.
   *
   * @param result the multi-solution result to display
   */
  public void displayResult(final MultiSolutionResult result) {
    LOGGER.trace("Displaying multi-solution result in TabPane...");

    platformRunLater.accept(
        () -> {
          // Clear existing tabs
          this.getTabs().clear();

          // Ensure we have tabs for all solutions and build them directly
          var solutionCount = Math.max(1, result.getSolutionCount());

          for (var i = 0; i < solutionCount; i++) {
            var solutionOpt = result.getSolution(i);
            var content = new SolutionTabContent();

            // Use the enhanced displaySolution method to handle tab creation and content
            var tab = content.displaySolution(solutionOpt.orElse(null), i);

            this.getTabs().add(tab);
          }
        });
  }
}
