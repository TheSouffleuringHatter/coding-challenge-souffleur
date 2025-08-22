package dev.coding_challenge_souffleur;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
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

  private static final String TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR =
      "#toggleProblemStatementButton";
  private static final String PROBLEM_STATEMENT_SECTION_SELECTOR = "#problemStatementSection";
  private static final KeyCombination.Modifier MATCHING_MODIFIER = KeyCombination.SHIFT_DOWN;
  private static final KeyCodeCombination HIDE_SHOW_KEY_CODE_COMBINATION =
      new KeyCodeCombination(KeyCode.W, MATCHING_MODIFIER);
  private static final KeyCodeCombination TAKE_SCREENSHOT_KEY_CODE_COMBINATION =
      new KeyCodeCombination(KeyCode.R, MATCHING_MODIFIER);
  private static final KeyCodeCombination RUN_MOCK_ANALYSIS_KEY_CODE_COMBINATION =
      new KeyCodeCombination(KeyCode.Z, MATCHING_MODIFIER);
  private static final KeyCodeCombination EXIT_KEY_CODE_COMBINATION =
      new KeyCodeCombination(KeyCode.Q, MATCHING_MODIFIER);
  private static final KeyCodeCombination SCROLL_DOWN_KEY_CODE_COMBINATION =
      new KeyCodeCombination(KeyCode.C, MATCHING_MODIFIER);
  private static final String CLOSE_BUTTON_SELECTOR = "#closeButton";
  private static final String MAIN_CONTAINER_SELECTOR = "#mainContainer";
  private static final String CONTENT_PANE_SELECTOR = "#contentPane";
  private static final String SOLUTION_CODE_FLOW_SELECTOR = "#solutionCodeFlow";
  private static final String SOLUTION_DESCRIPTION_FLOW_SELECTOR = "#solutionDescriptionFlow";
  private static final String EDGE_CASES_FLOW_SELECTOR = "#edgeCasesFlow";
  private static final String TIME_COMPLEXITY_FLOW_SELECTOR = "#timeComplexityFlow";
  private static final String SPACE_COMPLEXITY_FLOW_SELECTOR = "#spaceComplexityFlow";
  private JavaFxApplication javaFxApplication;
  private WeldContainer aiOverlayApplicationWeldContainer;

  @Init
  void init() {
    System.setProperty(JavaFxApplication.APPLICATION_TESTING_FLAG, Boolean.TRUE.toString());
    this.javaFxApplication = new JavaFxApplication();
    javaFxApplication.init();
  }

  @Start
  void start(final Stage stage) {
    javaFxApplication.start(stage);
    aiOverlayApplicationWeldContainer = this.javaFxApplication.getWeldContainer();
    assertTrue(aiOverlayApplicationWeldContainer.isRunning());

    var keyboardHookFacade =
        aiOverlayApplicationWeldContainer.select(KeyboardHookFacade.class).get();
    assertTrue(keyboardHookFacade.isRunning());
  }

  @Stop
  void stop() {
    assertFalse(Window.getWindows().isEmpty());
    javaFxApplication.stop();
    assertFalse(aiOverlayApplicationWeldContainer.isRunning());
    assertTrue(Window.getWindows().isEmpty());
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

  private void assertContentPane(final FxRobot robot) throws TimeoutException {
    robot.push(RUN_MOCK_ANALYSIS_KEY_CODE_COMBINATION);
    WaitForAsyncUtils.waitFor(
        3,
        TimeUnit.SECONDS,
        () ->
            !robot.lookup(CONTENT_PANE_SELECTOR).queryAll().isEmpty()
                && robot.lookup(CONTENT_PANE_SELECTOR).query().isVisible()
                && robot.lookup(SOLUTION_CODE_FLOW_SELECTOR).query().isVisible()
                && robot.lookup(SOLUTION_DESCRIPTION_FLOW_SELECTOR).query().isVisible());

    // this.scrollDown(robot, 4);
    WaitForAsyncUtils.waitFor(
        2,
        TimeUnit.SECONDS,
        () ->
            robot.lookup(EDGE_CASES_FLOW_SELECTOR).query().isVisible()
                && robot.lookup(TIME_COMPLEXITY_FLOW_SELECTOR).query().isVisible()
                && robot.lookup(SPACE_COMPLEXITY_FLOW_SELECTOR).query().isVisible());
  }

  private void assertHeader(final FxRobot robot) {
    verifyThat(MAIN_CONTAINER_SELECTOR, isVisible());
    verifyThat("#headerBox", isVisible());
    verifyThat(CONTENT_PANE_SELECTOR, isInvisible());

    robot.push(HIDE_SHOW_KEY_CODE_COMBINATION);
    assertTrue(robot.lookup(MAIN_CONTAINER_SELECTOR).queryAll().isEmpty());

    robot.push(HIDE_SHOW_KEY_CODE_COMBINATION);
    verifyThat(MAIN_CONTAINER_SELECTOR, isVisible());

    robot.push(TAKE_SCREENSHOT_KEY_CODE_COMBINATION);
    verifyThat("#screenshotPreview", isVisible());
  }

  private void assertClosing(final FxRobot robot) throws TimeoutException {
    var closeButton = robot.lookup(CLOSE_BUTTON_SELECTOR).queryButton();
    verifyThat(closeButton, isVisible());
    robot.push(EXIT_KEY_CODE_COMBINATION);
    WaitForAsyncUtils.waitFor(
        2,
        TimeUnit.SECONDS,
        () -> robot.lookup(TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR).queryAll().isEmpty());
  }

  private void assertProblemStatementSection(final FxRobot robot) {
    this.scrollDown(robot, 5);

    var showProblemStatementButton =
        robot.lookup(TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR).queryButton();
    robot.clickOn(showProblemStatementButton);

    this.scrollDown(robot, 5);
    verifyThat(PROBLEM_STATEMENT_SECTION_SELECTOR, isVisible());

    robot.clickOn(showProblemStatementButton);
    verifyThat(TOGGLE_PROBLEM_STATEMENT_BUTTON_SELECTOR, isVisible());
    verifyThat(PROBLEM_STATEMENT_SECTION_SELECTOR, isInvisible());
  }

  private void scrollDown(final FxRobot robot, final int times) {
    for (var i = 0; i < times; i++) {
      robot.push(SCROLL_DOWN_KEY_CODE_COMBINATION);
    }
  }
}
