package dev.coding_challenge_souffleur.view;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reusable UI component for displaying a single solution tab's content. Built with the Custom
 * Component + FXML pattern for clearer separation of structure and behavior.
 */
public class SolutionTabContent extends ScrollPane {

  private static final Logger LOGGER = LoggerFactory.getLogger(SolutionTabContent.class);
  private static final String SOLUTION_TAB_FXML = "SolutionTabContent.fxml";

  @FXML private FormattedTextFlow solutionDescriptionFlow;
  @FXML private FormattedTextFlow edgeCasesFlow;
  @FXML private FormattedTextFlow solutionCodeFlow;
  @FXML private FormattedTextFlow timeComplexityFlow;
  @FXML private FormattedTextFlow spaceComplexityFlow;

  public SolutionTabContent() {
    loadFXML();
  }

  private void loadFXML() {
    var loader = new FXMLLoader(getClass().getResource(SOLUTION_TAB_FXML));
    loader.setRoot(this);
    loader.setController(this);
    try {
      loader.load();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load " + SOLUTION_TAB_FXML, e);
    }
  }

  public void setSolutionDescription(final String content) {
    solutionDescriptionFlow.setFormattedContent(content, false);
  }

  public void setEdgeCases(final String content) {
    edgeCasesFlow.setFormattedContent(content, false);
  }

  public void setSolutionCode(final String content) {
    solutionCodeFlow.setFormattedContent(content, true);
  }

  public void setTimeComplexity(final String content) {
    timeComplexityFlow.setFormattedContent(content, false);
  }

  public void setSpaceComplexity(final String content) {
    spaceComplexityFlow.setFormattedContent(content, false);
  }
}
