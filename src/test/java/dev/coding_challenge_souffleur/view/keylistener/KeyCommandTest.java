package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.HideShowState;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import dev.coding_challenge_souffleur.view.ViewController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyCommandTest {

  @Mock private KeyCommandDependencies mockDependencies;
  @Mock private Runnable mockExitApplication;
  @Mock private HideShowState mockHideShowState;
  @Mock private ScreenshotDisplayService mockScreenshotService;
  @Mock private ViewController mockViewController;

  @Test
  void shouldExecuteExitCommand() {
    when(mockDependencies.exitApplication()).thenReturn(mockExitApplication);
    KeyCommand.EXIT.execute(mockDependencies);
    verify(mockExitApplication).run();
  }

  @Test
  void shouldExecuteHideShowCommand() {
    when(mockDependencies.hideShowState()).thenReturn(mockHideShowState);
    KeyCommand.HIDE_SHOW.execute(mockDependencies);
    verify(mockHideShowState).toggleVisibility();
  }

  @Test
  void shouldExecuteTakeScreenshotCommand() {
    when(mockDependencies.screenshotDisplayService()).thenReturn(mockScreenshotService);
    KeyCommand.TAKE_SCREENSHOT.execute(mockDependencies);
    verify(mockScreenshotService).takeScreenshotOfForegroundWindowAndShowPreview();
  }

  @Test
  void shouldExecuteRunAnalysisCommand() {
    when(mockDependencies.viewController()).thenReturn(mockViewController);
    KeyCommand.RUN_ANALYSIS.execute(mockDependencies);
    verify(mockViewController).executeMultiSolutionAnalysis();
  }

  @Test
  void shouldExecuteRunMockAnalysisCommand() {
    when(mockDependencies.viewController()).thenReturn(mockViewController);
    KeyCommand.RUN_MOCK_ANALYSIS.execute(mockDependencies);
    verify(mockViewController).executeMultiSolutionMockAnalysis();
  }

  @Test
  void shouldExecuteToggleProblemStatementCommand() {
    when(mockDependencies.viewController()).thenReturn(mockViewController);
    KeyCommand.TOGGLE_PROBLEM_STATEMENT.execute(mockDependencies);
    verify(mockViewController).toggleProblemStatement();
  }

  @Test
  void shouldHaveCorrectConfigProperties() {
    assertEquals("app.keyboard.key.exit", KeyCommand.EXIT.getKeyConfigProperty());
    assertEquals("app.keyboard.key.hide_show", KeyCommand.HIDE_SHOW.getKeyConfigProperty());
    assertEquals("app.keyboard.key.screenshot", KeyCommand.TAKE_SCREENSHOT.getKeyConfigProperty());
    assertEquals("app.keyboard.key.move_left", KeyCommand.MOVE_LEFT.getKeyConfigProperty());
    assertEquals("app.keyboard.key.move_right", KeyCommand.MOVE_RIGHT.getKeyConfigProperty());
    assertEquals("app.keyboard.key.move_down", KeyCommand.MOVE_DOWN.getKeyConfigProperty());
    assertEquals("app.keyboard.key.move_up", KeyCommand.MOVE_UP.getKeyConfigProperty());
    assertEquals("app.keyboard.key.run_analysis", KeyCommand.RUN_ANALYSIS.getKeyConfigProperty());
    assertEquals(
        "app.keyboard.key.run_mock_analysis", KeyCommand.RUN_MOCK_ANALYSIS.getKeyConfigProperty());
    assertEquals("app.keyboard.key.scroll_down", KeyCommand.SCROLL_DOWN.getKeyConfigProperty());
    assertEquals("app.keyboard.key.scroll_up", KeyCommand.SCROLL_UP.getKeyConfigProperty());
    assertEquals(
        "app.keyboard.key.switch_to_tab1", KeyCommand.SWITCH_TO_TAB1.getKeyConfigProperty());
    assertEquals(
        "app.keyboard.key.switch_to_tab2", KeyCommand.SWITCH_TO_TAB2.getKeyConfigProperty());
    assertEquals(
        "app.keyboard.key.switch_to_tab3", KeyCommand.SWITCH_TO_TAB3.getKeyConfigProperty());
    assertEquals(
        "app.keyboard.key.toggle_problem_statement",
        KeyCommand.TOGGLE_PROBLEM_STATEMENT.getKeyConfigProperty());
  }
}
