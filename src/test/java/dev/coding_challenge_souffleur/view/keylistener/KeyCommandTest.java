package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.ConfigurationKeys;
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
    assertEquals(ConfigurationKeys.APP_KEYBOARD_KEY_EXIT, KeyCommand.EXIT.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_HIDE_SHOW, KeyCommand.HIDE_SHOW.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SCREENSHOT,
        KeyCommand.TAKE_SCREENSHOT.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_LEFT, KeyCommand.MOVE_LEFT.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_RIGHT,
        KeyCommand.MOVE_RIGHT.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_DOWN, KeyCommand.MOVE_DOWN.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_MOVE_UP, KeyCommand.MOVE_UP.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_RUN_ANALYSIS,
        KeyCommand.RUN_ANALYSIS.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_RUN_MOCK_ANALYSIS,
        KeyCommand.RUN_MOCK_ANALYSIS.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SCROLL_DOWN,
        KeyCommand.SCROLL_DOWN.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SCROLL_UP, KeyCommand.SCROLL_UP.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB1,
        KeyCommand.SWITCH_TO_TAB1.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB2,
        KeyCommand.SWITCH_TO_TAB2.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_SWITCH_TO_TAB3,
        KeyCommand.SWITCH_TO_TAB3.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_TOGGLE_PROBLEM_STATEMENT,
        KeyCommand.TOGGLE_PROBLEM_STATEMENT.getKeyConfigProperty());
  }
}
