package dev.coding_challenge_souffleur.view.keylistener;

import java.util.function.Consumer;
import javafx.scene.control.ScrollPane;

enum KeyCommand {
  EXIT("app.keyboard.key.exit", (deps) -> deps.exitApplication().run()),

  HIDE_SHOW("app.keyboard.key.hide_show", (deps) -> deps.hideShowState().toggleVisibility()),

  TAKE_SCREENSHOT(
      "app.keyboard.key.screenshot",
      (deps) -> deps.screenshotDisplayService().takeScreenshotOfForegroundWindowAndShowPreview()),

  MOVE_LEFT(
      "app.keyboard.key.move_left",
      (deps) -> deps.stage().setX(deps.stage().getX() - Constants.MOVE_AMOUNT)),

  MOVE_RIGHT(
      "app.keyboard.key.move_right",
      (deps) -> deps.stage().setX(deps.stage().getX() + Constants.MOVE_AMOUNT)),

  MOVE_DOWN(
      "app.keyboard.key.move_down",
      (deps) -> deps.stage().setY(deps.stage().getY() + Constants.MOVE_AMOUNT)),

  MOVE_UP(
      "app.keyboard.key.move_up",
      (deps) -> deps.stage().setY(deps.stage().getY() - Constants.MOVE_AMOUNT)),

  RUN_ANALYSIS(
      "app.keyboard.key.run_analysis",
      (deps) -> deps.viewController().executeMultiSolutionAnalysis()),

  RUN_MOCK_ANALYSIS(
      "app.keyboard.key.run_mock_analysis",
      (deps) -> deps.viewController().executeMultiSolutionMockAnalysis()),

  SCROLL_DOWN("app.keyboard.key.scroll_down", deps -> scroll(deps, 0.2)),

  SCROLL_UP("app.keyboard.key.scroll_up", deps -> scroll(deps, -0.2)),

  SWITCH_TO_TAB1("app.keyboard.key.switch_to_tab1", deps -> switchToTab(deps, 0)),

  SWITCH_TO_TAB2("app.keyboard.key.switch_to_tab2", deps -> switchToTab(deps, 1)),

  SWITCH_TO_TAB3("app.keyboard.key.switch_to_tab3", deps -> switchToTab(deps, 2)),

  TOGGLE_PROBLEM_STATEMENT(
      "app.keyboard.key.toggle_problem_statement",
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
