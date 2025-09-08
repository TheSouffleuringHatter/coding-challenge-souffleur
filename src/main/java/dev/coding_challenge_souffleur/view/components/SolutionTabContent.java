package dev.coding_challenge_souffleur.view.components;

import dev.coding_challenge_souffleur.model.StreamingAnalysisResult;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;

/**
 * Reusable UI component for displaying a single solution tab's content. Built with the Custom
 * Component + FXML pattern for clearer separation of structure and behavior. Enhanced to handle
 * domain Solution objects directly.
 */
public class SolutionTabContent extends ScrollPane {

  private static final String SOLUTION_TAB_FXML = "SolutionTabContent.fxml";

  private final Tab tab;

  @FXML private FormattedTextFlow solutionDescriptionFlow;
  @FXML private FormattedTextFlow edgeCasesFlow;
  @FXML private FormattedTextFlow solutionCodeFlow;
  @FXML private FormattedTextFlow timeComplexityFlow;
  @FXML private FormattedTextFlow spaceComplexityFlow;

  public SolutionTabContent() {
    var loader = new FXMLLoader(getClass().getResource(SOLUTION_TAB_FXML));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load " + SOLUTION_TAB_FXML, e);
    }

    this.tab = new Tab();
    tab.setContent(this);
  }

  /**
   * Displays a solution using an existing tab (for updates) or creates a new tab (for initial
   * display). This unified method handles both creation and incremental updates elegantly.
   *
   * @param solution the solution to display
   * @param solutionIndex the 0-based index of this solution
   */
  void displaySolution(final StreamingAnalysisResult solution, final int solutionIndex) {
    updateTabTitle(solution, solutionIndex);

    solutionDescriptionFlow.setFormattedContent(solution.getSolutionDescription());
    edgeCasesFlow.setFormattedContent(solution.getEdgeCases());
    solutionCodeFlow.setFormattedContent(solution.getSolutionCode());
    timeComplexityFlow.setFormattedContent(solution.getTimeComplexity());
    spaceComplexityFlow.setFormattedContent(solution.getSpaceComplexity());
  }

  Tab getTab() {
    return this.tab;
  }

  private void updateTabTitle(final StreamingAnalysisResult solution, final int solutionIndex) {
    // Set tab title from solution title or default
    var newTabTitle = "(" + (solutionIndex + 1) + ") Solution";
    if (solution.getSolutionTitle() != null) {
      newTabTitle = "(" + (solutionIndex + 1) + ") " + solution.getSolutionTitle();
    }

    // Only update the title if it would change
    if (!newTabTitle.equals(tab.getText())) {
      tab.setText(newTabTitle);
    }
  }
}
