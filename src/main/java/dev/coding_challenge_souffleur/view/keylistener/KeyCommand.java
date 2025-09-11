package dev.coding_challenge_souffleur.view.keylistener;

import dev.coding_challenge_souffleur.ConfigurationKeys;
import java.util.function.Consumer;
import javafx.scene.control.ScrollPane;

enum KeyCommand {
  EXIT(ConfigurationKeys.APP_KEYBOARD_KEY_EXIT, (deps) -> deps.exitApplication().run()),

  HIDE_SHOW(
      ConfigurationKeys.APP_KEYBOARD_KEY_HIDE_SHOW,
      (deps) -> deps.hideShowState().toggleVisibility()),

  TAKE_SCREENSHOT(
      ConfigurationKeys.APP_KEYBOARD_KEY_SCREENSHOT,
      (deps) -> deps.screenshotDisplayService().takeScreenshotOfForegroundWindowAndShowPreview()),

  MOVE_LEFT(
      ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_LEFT,
      (deps) -> deps.stage().setX(deps.stage().getX() - Constants.MOVE_AMOUNT)),

  MOVE_RIGHT(
      ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_RIGHT,
      (deps) -> deps.stage().setX(deps.stage().getX() + Constants.MOVE_AMOUNT)),

  MOVE_DOWN(
      ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_DOWN,
      (deps) -> deps.stage().setY(deps.stage().getY() + Constants.MOVE_AMOUNT)),

  MOVE_UP(
      ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_UP,
      (deps) -> deps.stage().setY(deps.stage().getY() - Constants.MOVE_AMOUNT)),

  RUN_ANALYSIS(
      ConfigurationKeys.APP_KEYBOARD_KEY_RUN_ANALYSIS,
      (deps) -> deps.viewController().executeMultiSolutionAnalysis()),

  RUN_MOCK_ANALYSIS(
      ConfigurationKeys.APP_KEYBOARD_KEY_RUN_MOCK_ANALYSIS,
      (deps) -> deps.viewController().executeMultiSolutionMockAnalysis()),

  SCROLL_DOWN(ConfigurationKeys.APP_KEYBOARD_KEY_SCROLL_DOWN, deps -> scroll(deps, 0.2)),

  SCROLL_UP(ConfigurationKeys.APP_KEYBOARD_KEY_SCROLL_UP, deps -> scroll(deps, -0.2)),

  SWITCH_TO_TAB1(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB1, deps -> switchToTab(deps, 0)),

  SWITCH_TO_TAB2(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB2, deps -> switchToTab(deps, 1)),

  SWITCH_TO_TAB3(ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB3, deps -> switchToTab(deps, 2)),

  TOGGLE_PROBLEM_STATEMENT(
      ConfigurationKeys.APP_KEYBOARD_KEY_TOGGLE_PROBLEM_STATEMENT,
      (deps) -> deps.viewController().toggleProblemStatement());

  private final String keyConfigProperty;
  private final Consumer<KeyCommandDependencies> action;

  KeyCommand(String keyConfigProperty, Consumer<KeyCommandDependencies> action) {
    this.keyConfigProperty = keyConfigProperty;
    this.action = action;
  }

  private static void scroll(final KeyCommandDependencies deps, final double deltaValue) {
    var tabPane = deps.multiSolutionTabPane();
    if (tabPane != null && tabPane.isVisible()) {
      var selectedTab = tabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof ScrollPane scrollPane) {
        var vvalue = scrollPane.getVvalue();
        var newValue = Math.clamp(vvalue + deltaValue, 0.0, 1.0);
        scrollPane.setVvalue(newValue);
      }
    }
  }

  private static void switchToTab(final KeyCommandDependencies deps, final int tabIndex) {
    var tabPane = deps.multiSolutionTabPane();
    if (tabPane != null && tabPane.isVisible() && tabPane.getTabs().size() > tabIndex) {
      tabPane.getSelectionModel().select(tabIndex);
    }
  }

  String getKeyConfigProperty() {
    return keyConfigProperty;
  }

  void execute(final KeyCommandDependencies dependencies) {
    action.accept(dependencies);
  }

  private static class Constants {
    static final int MOVE_AMOUNT = 30;
  }
}
