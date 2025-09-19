package dev.coding_challenge_souffleur;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.model.ProgrammingLanguage;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.Modifier;
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
  private static final String LANGUAGE_SELECTOR_SELECTOR = "#languageSelector";

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
  private KeyCodeCombination languagePreviousKeyCombination;
  private KeyCodeCombination languageNextKeyCombination;

  private JavaFxApplication javaFxApplication;
  private WeldContainer weldContainer;

  private static Modifier convertWin32VKsToModifier(List<Win32VK> win32VKs) {
    if (win32VKs.size() == 2
        && (win32VKs.contains(Win32VK.VK_LSHIFT) || win32VKs.contains(Win32VK.VK_RSHIFT))) {
      return KeyCombination.SHIFT_DOWN;
    }

    if (win32VKs.size() == 2
        && (win32VKs.contains(Win32VK.VK_LCONTROL) || win32VKs.contains(Win32VK.VK_RCONTROL))) {
      return KeyCombination.CONTROL_DOWN;
    }

    throw new IllegalArgumentException("Unsupported Win32VKs: " + win32VKs);
  }

  private static KeyCode convertWin32VKToKeyCode(Win32VK win32VK) {
    return switch (win32VK) {
      case VK_W -> KeyCode.W;
      case VK_R -> KeyCode.R;
      case VK_Z -> KeyCode.Z;
      case VK_Q -> KeyCode.Q;
      case VK_V -> KeyCode.V;
      case VK_ESCAPE -> KeyCode.ESCAPE;
      case VK_1 -> KeyCode.DIGIT1;
      case VK_2 -> KeyCode.DIGIT2;
      case VK_3 -> KeyCode.DIGIT3;
      case VK_5 -> KeyCode.DIGIT5;
      case VK_6 -> KeyCode.DIGIT6;
      case VK_7 -> KeyCode.DIGIT7;
      default -> throw new IllegalArgumentException("Unsupported Win32VK: " + win32VK);
    };
  }

  private static String getFlowText(final Node flowNode) {
    assertInstanceOf(TextFlow.class, flowNode);

    var textFlow = (TextFlow) flowNode;
    var sb = new StringBuilder();
    for (var child : textFlow.getChildren()) {
      if (child instanceof Text text) {
        sb.append(text.getText());
      }
    }

    return sb.toString();
  }

  private static void assertSelectedTabHasCoreNodes(final VBox contentBox) {
    assertFalse(contentBox.lookupAll(SOLUTION_DESCRIPTION_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(EDGE_CASES_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(SOLUTION_CODE_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(TIME_COMPLEXITY_FLOW_SELECTOR).isEmpty());
    assertFalse(contentBox.lookupAll(SPACE_COMPLEXITY_FLOW_SELECTOR).isEmpty());
  }

  private static KeyCodeCombination createKeyCombination(
      Win32VK win32VK, Modifier matchingModifier) {
    var keyCode = convertWin32VKToKeyCode(win32VK);
    return new KeyCodeCombination(keyCode, matchingModifier);
  }

  private void initializeKeyboardCombinations() {
    var config = ConfigProvider.getConfig();

    var modifierKeys =
        config.getValues(ConfigurationKeys.APP_KEYBOARD_MODIFIER_KEYS, Win32VK.class);
    var matchingModifier = convertWin32VKsToModifier(modifierKeys);

    hideShowKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_HIDE_SHOW, String.class)),
            matchingModifier);
    screenshotKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_SCREENSHOT, String.class)),
            matchingModifier);
    runMockAnalysisKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(
                    ConfigurationKeys.APP_KEYBOARD_KEY_RUN_MOCK_ANALYSIS, String.class)),
            matchingModifier);
    exitKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_EXIT, String.class)),
            matchingModifier);
    scrollDownKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_SCROLL_DOWN, String.class)),
            matchingModifier);
    toggleProblemStatementKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(
                    ConfigurationKeys.APP_KEYBOARD_KEY_TOGGLE_PROBLEM_STATEMENT, String.class)),
            matchingModifier);
    switchToTab1Combination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB1, String.class)),
            matchingModifier);
    switchToTab2Combination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB2, String.class)),
            matchingModifier);
    switchToTab3Combination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB3, String.class)),
            matchingModifier);
    languagePreviousKeyCombination =
        createKeyCombination(
            Win32VK.valueOf(
                config.getValue(
                    ConfigurationKeys.APP_KEYBOARD_KEY_CODING_LANGUAGE_PREVIOUS, String.class)),
            matchingModifier);
    var languageNextKeyString =
        config.getValue(ConfigurationKeys.APP_KEYBOARD_KEY_CODING_LANGUAGE_NEXT, String.class);
    LOGGER.debug("Language Next Key Config: {}", languageNextKeyString);
    languageNextKeyCombination =
        createKeyCombination(Win32VK.valueOf(languageNextKeyString), matchingModifier);
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
    weldContainer = this.javaFxApplication.getWeldContainer();

    assertTrue(weldContainer.isRunning());

    var keyboardHookFacade = weldContainer.select(KeyboardHookFacade.class).get();
    assertTrue(keyboardHookFacade.isRunning());

    LOGGER.debug("Application started successfully in test mode");
  }

  @Stop
  @SuppressWarnings("unused")
  void stop() {
    var windowsBeforeStop = Window.getWindows().size();
    assertFalse(Window.getWindows().isEmpty());

    javaFxApplication.stop();

    assertFalse(weldContainer.isRunning());
    assertTrue(Window.getWindows().isEmpty());

    LOGGER.debug("Application stopped successfully, {} windows were closed", windowsBeforeStop);
  }

  @Test
  void testApplicationStartsAndExits(final FxRobot robot) throws TimeoutException {
    var initialWindow = robot.window(0);
    assertTrue(initialWindow.isShowing());

    assertHeader(robot);
    assertLanguageCycling(robot);
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

  private void assertLanguageCycling(final FxRobot robot) {
    verifyThat(LANGUAGE_SELECTOR_SELECTOR, isVisible());

    var languageSelector = robot.lookup(LANGUAGE_SELECTOR_SELECTOR).queryAs(ComboBox.class);
    var initialLanguage = (ProgrammingLanguage) languageSelector.getValue();
    LOGGER.debug("Initial language: {}", initialLanguage);

    // Test next language cycling
    LOGGER.debug("Pressing language next key combination");
    robot.push(languageNextKeyCombination);
    robot.sleep(500);
    var nextLanguage = (ProgrammingLanguage) languageSelector.getValue();
    LOGGER.debug("Language after next key: {}", nextLanguage);
    assertNotEquals(nextLanguage, initialLanguage);

    // Test previous language cycling
    robot.push(languagePreviousKeyCombination);
    robot.sleep(500);
    var backToInitial = (ProgrammingLanguage) languageSelector.getValue();
    assertEquals(backToInitial, initialLanguage);

    // Test that cycling through all languages works
    var allLanguages = ProgrammingLanguage.values();
    for (var i = 0; i < allLanguages.length; i++) {
      robot.push(languageNextKeyCombination);
      robot.sleep(50);
    }
    var backToStart = (ProgrammingLanguage) languageSelector.getValue();
    assertEquals(backToStart, initialLanguage);
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
