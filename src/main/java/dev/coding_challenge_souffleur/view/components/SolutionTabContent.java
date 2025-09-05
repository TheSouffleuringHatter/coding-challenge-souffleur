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
  }

  /**
   * Displays a solution with the given index, handling tab title logic internally. Returns a
   * configured Tab object ready for insertion into a TabPane.
   *
   * @param solution the solution to display
   * @param solutionIndex the 0-based index of this solution
   * @return a configured Tab containing this solution content
   */
  public Tab displaySolution(final StreamingAnalysisResult solution, final int solutionIndex) {
    var tab = new Tab();
    tab.setContent(this);

    // Set tab title from solution title or default
    var tabTitle = "Solution " + (solutionIndex + 1);
    if (solution != null && solution.getSolutionTitle() != null) {
      tabTitle = "(" + (solutionIndex + 1) + ") " + solution.getSolutionTitle();
    }
    tab.setText(tabTitle);

    // If solution is null, rely on FXML defaults
    if (solution != null) {
      setSolutionDescription(solution.getSolutionDescription());
      setEdgeCases(solution.getEdgeCases());
      setSolutionCode(solution.getSolutionCode());
      setTimeComplexity(solution.getTimeComplexity());
      setSpaceComplexity(solution.getSpaceComplexity());
    }

    return tab;
  }

  public void setSolutionDescription(final String content) {
    solutionDescriptionFlow.setFormattedContent(content);
  }

  public void setEdgeCases(final String content) {
    edgeCasesFlow.setFormattedContent(content);
  }

  public void setSolutionCode(final String content) {
    solutionCodeFlow.setFormattedCodeContent(content);
  }

  public void setTimeComplexity(final String content) {
    timeComplexityFlow.setFormattedContent(content);
  }

  public void setSpaceComplexity(final String content) {
    spaceComplexityFlow.setFormattedContent(content);
  }
}
