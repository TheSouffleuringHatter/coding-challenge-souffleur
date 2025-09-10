package dev.coding_challenge_souffleur;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.sun.jna.platform.win32.Win32VK;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Init;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;
import org.testfx.util.WaitForAsyncUtils;
import windowskeyboardhook.KeyboardHookFacade;

@ExtendWith(ApplicationExtension.class)
@Isolated
class JavaFxApplicationSmokeTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxApplicationSmokeTest.class);

  private static final int MAX_SCROLL_ATTEMPTS = 50;

  /**
   * In case of testing use SHIFT as modifier; in tests, any SHIFT acts as the modifier key.
   *
   * <p>Background: It is not possible to isolate KeyCombination to usage of right CTRL.
   */
  private static final KeyCombination.Modifier MATCHING_MODIFIER = KeyCombination.SHIFT_DOWN;

  // Configuration-based key combinations - loaded from MicroProfile Config
  private KeyCodeCombination hideShowKeyCombination;
  private KeyCodeCombination screenshotKeyCombination;
  private KeyCodeCombination runMockAnalysisKeyCombination;
  private KeyCodeCombination exitKeyCombination;
  private KeyCodeCombination scrollDownKeyCombination;
  private KeyCodeCombination toggleProblemStatementKeyCombination;
  private KeyCodeCombination switchToTab1Combination;
  private KeyCodeCombination switchToTab2Combination;
  private KeyCodeCombination switchToTab3Combination;

  private static final String TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR =
      "#toggleProblemStatementButton";
  private static final String PROBLEM_STATEMENT_SECTION_SELECTOR = "#problemStatementSection";
  private static final String CLOSE_BUTTON_SELECTOR = "#closeButton";
  private static final String MAIN_CONTAINER_SELECTOR = "#mainContainer";
  private static final String CONTENT_PANE_SELECTOR = "#contentPane";
  private static final String SOLUTION_CODE_FLOW_SELECTOR = "#solutionCodeFlow";
  private static final String SOLUTION_DESCRIPTION_FLOW_SELECTOR = "#solutionDescriptionFlow";
  private static final String EDGE_CASES_FLOW_SELECTOR = "#edgeCasesFlow";
  private static final String TIME_COMPLEXITY_FLOW_SELECTOR = "#timeComplexityFlow";
  private static final String SPACE_COMPLEXITY_FLOW_SELECTOR = "#spaceComplexityFlow";
  private static final String SOLUTION_TAB_PANE_SELECTOR = "#solutionTabPane";
  private static final String STATUS_LABEL_SELECTOR = "#statusLabel";
  private static final String HEADER_BOX_SELECTOR = "#headerBox";
  private static final String SCREENSHOT_PREVIEW_SELECTOR = "#screenshotPreview";

  private JavaFxApplication javaFxApplication;
  private WeldContainer aiOverlayApplicationWeldContainer;

  private static KeyCodeCombination createKeyCombination(Win32VK win32VK) {
    var keyCode = convertWin32VKToKeyCode(win32VK);
    return new KeyCodeCombination(keyCode, MATCHING_MODIFIER);
  }

  private static KeyCode convertWin32VKToKeyCode(Win32VK win32VK) {
    return switch (win32VK) {
      case VK_W -> KeyCode.W;
      case VK_R -> KeyCode.R;
      case VK_Z -> KeyCode.Z;
      case VK_Q -> KeyCode.Q;
      case VK_5 -> KeyCode.DIGIT5;
      case VK_V -> KeyCode.V;
      case VK_1 -> KeyCode.DIGIT1;
      case VK_2 -> KeyCode.DIGIT2;
      case VK_3 -> KeyCode.DIGIT3;
      default -> throw new IllegalArgumentException("Unsupported Win32VK: " + win32VK);
    };
  }

  private void initializeKeyboardCombinations() {
    var config = ConfigProvider.getConfig();

    hideShowKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.hide_show", String.class)));
    screenshotKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.screenshot", String.class)));
    runMockAnalysisKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.run_mock_analysis", String.class)));
    exitKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.exit", String.class)));
    scrollDownKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.scroll_down", String.class)));
    toggleProblemStatementKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue("app.keyboard.key.toggle_problem_statement", String.class)));
    switchToTab1Combination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.switch_to_tab1", String.class)));
    switchToTab2Combination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.switch_to_tab2", String.class)));
    switchToTab3Combination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue("app.keyboard.key.switch_to_tab3", String.class)));
  }

  private void scrollDownUntilVisible(final FxRobot robot, String nodeQuery) {
    for (var i = 0; i < MAX_SCROLL_ATTEMPTS; i++) {
      robot.push(scrollDownKeyCombination);
      robot.sleep(100);

      try {
        // This uses TestFX's internal visibility logic which considers viewport bounds
        robot.point(nodeQuery).query();
        return; // If we can get a point, the node is actually shown on screen
      } catch (RuntimeException e) {
        LOGGER.trace(
            "Node {} not accessible after {}/{} scroll attempts: {}",
            nodeQuery,
            i,
            MAX_SCROLL_ATTEMPTS,
            e.getMessage());
      }
    }

    fail(
        "Failed to make node "
            + nodeQuery
            + " visible after "
            + MAX_SCROLL_ATTEMPTS
            + " scroll attempts");
  }

  private static String getFlowText(final Node flowNode) {
    if (flowNode instanceof TextFlow textFlow) {
      var sb = new StringBuilder();
      for (var child : textFlow.getChildren()) {
        if (child instanceof Text text) {
          sb.append(text.getText());
        }
      }
      return sb.toString();
    }

    LOGGER.debug("Node {} is not a TextFlow", flowNode);
    return "";
  }

  private static void assertSelectedTabHasCoreNodes(final VBox contentBox) {
    assertFalse(contentBox.lookupAll(SOLUTION_DESCRIPTION_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(EDGE_CASES_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(SOLUTION_CODE_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(TIME_COMPLEXITY_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(SPACE_COMPLEXITY_FLOW_SELECTOR).isEmpty());
  }

  @Init
  @SuppressWarnings("unused")
  void init() {
    this.javaFxApplication = new JavaFxApplication();
    javaFxApplication.init();
    initializeKeyboardCombinations();
  }

  @Start
  @SuppressWarnings("unused")
  void start(final Stage stage) {
    javaFxApplication.start(stage);
    aiOverlayApplicationWeldContainer = this.javaFxApplication.getWeldContainer();

    assertTrue(aiOverlayApplicationWeldContainer.isRunning());

    var keyboardHookFacade =
        aiOverlayApplicationWeldContainer.select(KeyboardHookFacade.class).get();
    assertTrue(keyboardHookFacade.isRunning());

    LOGGER.debug("Application started successfully in test mode");
  }

  @Stop
  @SuppressWarnings("unused")
  void stop() {
    var windowsBeforeStop = Window.getWindows().size();
    assertFalse(Window.getWindows().isEmpty());

    javaFxApplication.stop();

    assertFalse(aiOverlayApplicationWeldContainer.isRunning());
    assertTrue(Window.getWindows().isEmpty());

    LOGGER.debug("Application stopped successfully, {} windows were closed", windowsBeforeStop);
  }

  @Test
  void testApplicationStartsAndExits(final FxRobot robot) throws TimeoutException {
    var initialWindow = robot.window(0);
    assertTrue(initialWindow.isShowing());

    assertHeader(robot);
    assertContentPane(robot);
    assertProblemStatementSection(robot);
    assertClosing(robot);
  }

  private void assertHeader(final FxRobot robot) {
    verifyThat(MAIN_CONTAINER_SELECTOR, isVisible());
    verifyThat(HEADER_BOX_SELECTOR, isVisible());
    verifyThat(CONTENT_PANE_SELECTOR, isInvisible());

    robot.push(hideShowKeyCombination);
    assertTrue(robot.lookup(MAIN_CONTAINER_SELECTOR).queryAll().isEmpty());

    robot.push(hideShowKeyCombination);
    verifyThat(MAIN_CONTAINER_SELECTOR, isVisible());

    robot.push(screenshotKeyCombination);
    verifyThat(SCREENSHOT_PREVIEW_SELECTOR, isVisible());
  }

  private void assertContentPane(final FxRobot robot) throws TimeoutException {
    runMockAnalysisAndWait(robot);

    var tabPane = robot.lookup(SOLUTION_TAB_PANE_SELECTOR).queryAs(TabPane.class);
    WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> tabPane.getTabs().size() == 3);

    // Initial tab (index 0) should be selected by default; assert content via snippet
    var contentBox = getSelectedTabContentBox(tabPane);
    assertSelectedTabHasCoreNodes(contentBox);
    var initialDescText = getFlowText(contentBox.lookup(SOLUTION_DESCRIPTION_FLOW_SELECTOR));
    assertTrue(initialDescText.contains("combinations of indices"));
    assertTrue(initialDescText.contains("Nested loops"));

    switchToTabAndAssertContent(robot, tabPane, 1, switchToTab2Combination, "HASH MAP");
    switchToTabAndAssertContent(robot, tabPane, 2, switchToTab3Combination, "FREQUENCY TRACKING");
    switchToTabAndAssertContent(robot, tabPane, 0, switchToTab1Combination, "Nested loops");
  }

  private void runMockAnalysisAndWait(final FxRobot robot) throws TimeoutException {
    robot.push(runMockAnalysisKeyCombination);

    // Wait until the content pane is visible and the solution tab pane is present
    WaitForAsyncUtils.waitFor(
        2, TimeUnit.SECONDS, () -> !robot.lookup(CONTENT_PANE_SELECTOR).queryAll().isEmpty());
    WaitForAsyncUtils.waitFor(
        2, TimeUnit.SECONDS, () -> robot.lookup(CONTENT_PANE_SELECTOR).query().isVisible());

    // Wait until analysis is marked complete to avoid background tasks running after exit
    WaitForAsyncUtils.waitFor(
        2,
        TimeUnit.SECONDS,
        () -> {
          var statusLabel = robot.lookup(STATUS_LABEL_SELECTOR).queryAs(Label.class);
          return statusLabel.getText().toLowerCase().contains("complete");
        });
    WaitForAsyncUtils.waitFor(
        2, TimeUnit.SECONDS, () -> !robot.lookup(SOLUTION_TAB_PANE_SELECTOR).queryAll().isEmpty());
  }

  private VBox getSelectedTabContentBox(final TabPane tabPane) {
    var selectedTab = tabPane.getSelectionModel().getSelectedItem();
    var scrollPane = (ScrollPane) selectedTab.getContent();
    return (VBox) scrollPane.getContent();
  }

  private void switchToTabAndAssertContent(
      final FxRobot robot,
      final TabPane tabPane,
      final int index,
      final KeyCodeCombination combo,
      final String expectedSnippet)
      throws TimeoutException {

    // Switch to tab with simple retry
    robot.push(combo);
    try {
      WaitForAsyncUtils.waitFor(
          2, TimeUnit.SECONDS, () -> tabPane.getSelectionModel().getSelectedIndex() == index);
    } catch (TimeoutException e) {
      // One retry attempt
      robot.push(combo);
      WaitForAsyncUtils.waitFor(
          2, TimeUnit.SECONDS, () -> tabPane.getSelectionModel().getSelectedIndex() == index);
    }

    // Assert content
    var contentBox = getSelectedTabContentBox(tabPane);
    assertSelectedTabHasCoreNodes(contentBox);
    var descFlow = contentBox.lookup(SOLUTION_DESCRIPTION_FLOW_SELECTOR);
    var text = getFlowText(descFlow);
    assertTrue(text.contains(expectedSnippet));
  }

  private void assertClosing(final FxRobot robot) throws TimeoutException {
    var closeButton = robot.lookup(CLOSE_BUTTON_SELECTOR).queryButton();
    verifyThat(closeButton, isVisible());

    robot.push(exitKeyCombination);

    WaitForAsyncUtils.waitFor(
        2, TimeUnit.SECONDS, () -> robot.lookup(MAIN_CONTAINER_SELECTOR).queryAll().isEmpty());
  }

  private void assertProblemStatementSection(final FxRobot robot) {
    scrollDownUntilVisible(robot, TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR);

    robot.push(toggleProblemStatementKeyCombination);
    scrollDownUntilVisible(robot, PROBLEM_STATEMENT_SECTION_SELECTOR);

    robot.push(toggleProblemStatementKeyCombination);
    verifyThat(TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR, isVisible());
    verifyThat(PROBLEM_STATEMENT_SECTION_SELECTOR, isInvisible());
  }
}
