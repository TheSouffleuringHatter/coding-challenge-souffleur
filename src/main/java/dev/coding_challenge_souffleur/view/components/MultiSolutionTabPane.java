package dev.coding_challenge_souffleur.view.components;

import dev.coding_challenge_souffleur.model.MultiSolutionResult;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom JavaFX component extending TabPane that handles the display of multiple solutions. This
 * component understands domain objects and manages its own display logic. Note: This is not a
 * CDI-managed bean because JavaFX components cannot be proxied.
 */
public class MultiSolutionTabPane extends TabPane {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiSolutionTabPane.class);

  public MultiSolutionTabPane() {
    this.getStyleClass().add("solution-tabs");
    this.setId("solutionTabPane");
  }

  /**
   * Displays the given MultiSolutionResult by incrementally updating solution tabs. This method
   * preserves existing tabs and only updates their content, preventing flickering and maintaining
   * user's tab selection state during streaming updates.
   *
   * @param multiSolutionResult the multi-solution multiSolutionResult to display
   */
  public void displayResult(final MultiSolutionResult multiSolutionResult) {
    LOGGER.trace("Displaying multi-solution multiSolutionResult in TabPane...");

    var solutionCount = Math.max(1, multiSolutionResult.getSolutionCount());
    for (var i = 0; i < solutionCount; i++) {
      LOGGER.trace("Updating tab content for solution {}", i + 1);

      final var optionalSolution = multiSolutionResult.getSolution(i);
      if (optionalSolution.isEmpty()) {
        return;
      }
      var solution = optionalSolution.get();

      var tabContent = getOrCreateTabContent(i);
      tabContent.displaySolution(solution, i);

      // Force layout pass to trigger window resize for content changes
      this.requestLayout();

      // Also request layout on the scene root to ensure full layout recalculation
      var scene = this.getScene();
      if (scene != null && scene.getRoot() != null) {
        scene.getRoot().requestLayout();
      }
    }
  }

  private SolutionTabContent getOrCreateTabContent(final int i) {
    SolutionTabContent tabContent;
    if (i >= this.getTabs().size()) {
      tabContent = new SolutionTabContent();
      var tab = tabContent.getTab();
      this.getTabs().add(tab);
      LOGGER.trace("Adding new tab for solution {}", i + 1);
    } else {
      var tab = this.getTabs().get(i);
      tabContent = (SolutionTabContent) tab.getContent();
    }

    return tabContent;
  }
}
