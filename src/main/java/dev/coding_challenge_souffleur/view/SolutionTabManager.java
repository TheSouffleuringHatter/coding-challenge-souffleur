package dev.coding_challenge_souffleur.view;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages creation and organization of solution tabs in the UI. */
final class SolutionTabManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(SolutionTabManager.class);
  private static final String DEFAULT_TAB_TITLE = "Solution";

  private SolutionTabManager() {
    // Utility class
  }

  /** Creates a new solution tab with all necessary UI components. */
  public static Tab createSolutionTab(final String title) {
    var tab = new Tab(title.isEmpty() ? DEFAULT_TAB_TITLE : title);
    tab.setClosable(false);

    var scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    scrollPane.getStyleClass().add("solution-scroll-pane");

    var contentBox = new VBox();
    contentBox.getStyleClass().add("solution-content");

    // Solution Description
    var solutionDescriptionFlow = new FormattedTextFlow();
    solutionDescriptionFlow.getStyleClass().add("text-content");
    contentBox.getChildren().add(solutionDescriptionFlow);

    // Edge Cases Section
    var edgeCasesSection = new VBox();
    edgeCasesSection.getStyleClass().add("content-section");
    var edgeCasesLabel = new Label("Edge Cases");
    edgeCasesLabel.getStyleClass().add("section-title");
    var edgeCasesFlow = new FormattedTextFlow();
    edgeCasesFlow.getStyleClass().add("text-content");
    edgeCasesSection.getChildren().addAll(edgeCasesLabel, edgeCasesFlow);
    contentBox.getChildren().add(edgeCasesSection);

    // Solution Code
    var solutionCodeFlow = new FormattedTextFlow();
    solutionCodeFlow.getStyleClass().add("code-block");
    contentBox.getChildren().add(solutionCodeFlow);

    // Time Complexity Section
    var timeComplexitySection = new VBox();
    timeComplexitySection.getStyleClass().add("content-section");
    var timeComplexityLabel = new Label("Time Complexity");
    timeComplexityLabel.getStyleClass().add("section-title");
    var timeComplexityFlow = new FormattedTextFlow();
    timeComplexityFlow.getStyleClass().add("text-content");
    timeComplexitySection.getChildren().addAll(timeComplexityLabel, timeComplexityFlow);
    contentBox.getChildren().add(timeComplexitySection);

    // Space Complexity Section
    var spaceComplexitySection = new VBox();
    spaceComplexitySection.getStyleClass().add("content-section");
    var spaceComplexityLabel = new Label("Space Complexity");
    spaceComplexityLabel.getStyleClass().add("section-title");
    var spaceComplexityFlow = new FormattedTextFlow();
    spaceComplexityFlow.getStyleClass().add("text-content");
    spaceComplexitySection.getChildren().addAll(spaceComplexityLabel, spaceComplexityFlow);
    contentBox.getChildren().add(spaceComplexitySection);

    scrollPane.setContent(contentBox);
    tab.setContent(scrollPane);

    // Store references to text flows in tab userData for later access
    var tabData =
        new SolutionTabData(
            solutionDescriptionFlow,
            edgeCasesFlow,
            solutionCodeFlow,
            timeComplexityFlow,
            spaceComplexityFlow);
    tab.setUserData(tabData);

    LOGGER.debug("Created solution tab: {}", title);
    return tab;
  }

  /** Gets the text flow components from a solution tab. */
  public static SolutionTabData getTabData(final Tab tab) {
    return (SolutionTabData) tab.getUserData();
  }

  /** Ensures the TabPane has at least the specified number of tabs. */
  public static void ensureTabCount(final TabPane tabPane, final int requiredCount) {
    while (tabPane.getTabs().size() < requiredCount) {
      var tabIndex = tabPane.getTabs().size() + 1;
      var tab = createSolutionTab("Solution " + tabIndex);
      tabPane.getTabs().add(tab);
    }
  }

  public record SolutionTabData(
      FormattedTextFlow solutionDescriptionFlow,
      FormattedTextFlow edgeCasesFlow,
      FormattedTextFlow solutionCodeFlow,
      FormattedTextFlow timeComplexityFlow,
      FormattedTextFlow spaceComplexityFlow) {}
}
