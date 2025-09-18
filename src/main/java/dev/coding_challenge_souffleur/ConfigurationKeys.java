package dev.coding_challenge_souffleur;

public final class ConfigurationKeys {

  // Anthropic API Configuration
  public static final String ANTHROPIC_API_KEY = "anthropic.api.key";
  public static final String ANTHROPIC_MODEL = "anthropic.model";

  // Application Configuration
  public static final String APP_EXIT_PLATFORM_ON_CLOSE = "app.exit.platform.on.close";
  public static final String APP_STAGE_CREATION_ASYNC = "app.stage.creation.async";

  // Keyboard Configuration
  public static final String APP_KEYBOARD_FILTER_INJECTED_KEYS =
      "app.keyboard.filter.injected.keys";
  public static final String APP_KEYBOARD_MODIFIER_KEYS = "app.keyboard.modifier.keys";

  // Keyboard Shortcuts
  public static final String APP_KEYBOARD_KEY_EXIT = "app.keyboard.key.exit";
  public static final String APP_KEYBOARD_KEY_HIDE_SHOW = "app.keyboard.key.hide_show";
  public static final String APP_KEYBOARD_KEY_SCREENSHOT = "app.keyboard.key.screenshot";
  public static final String APP_KEYBOARD_KEY_MOVE_LEFT = "app.keyboard.key.move_left";
  public static final String APP_KEYBOARD_KEY_MOVE_RIGHT = "app.keyboard.key.move_right";
  public static final String APP_KEYBOARD_KEY_MOVE_DOWN = "app.keyboard.key.move_down";
  public static final String APP_KEYBOARD_KEY_MOVE_UP = "app.keyboard.key.move_up";
  public static final String APP_KEYBOARD_KEY_RUN_ANALYSIS = "app.keyboard.key.run_analysis";
  public static final String APP_KEYBOARD_KEY_RUN_MOCK_ANALYSIS =
      "app.keyboard.key.run_mock_analysis";
  public static final String APP_KEYBOARD_KEY_SCROLL_DOWN = "app.keyboard.key.scroll_down";
  public static final String APP_KEYBOARD_KEY_SCROLL_UP = "app.keyboard.key.scroll_up";
  public static final String APP_KEYBOARD_KEY_SWITCH_TO_TAB1 = "app.keyboard.key.switch_to_tab1";
  public static final String APP_KEYBOARD_KEY_SWITCH_TO_TAB2 = "app.keyboard.key.switch_to_tab2";
  public static final String APP_KEYBOARD_KEY_SWITCH_TO_TAB3 = "app.keyboard.key.switch_to_tab3";
  public static final String APP_KEYBOARD_KEY_TOGGLE_PROBLEM_STATEMENT =
      "app.keyboard.key.toggle_problem_statement";
  public static final String APP_KEYBOARD_KEY_LANGUAGE_PREVIOUS = "app.keyboard.key.language_previous";
  public static final String APP_KEYBOARD_KEY_LANGUAGE_NEXT = "app.keyboard.key.language_next";

  // Screenshot Configuration
  public static final String SAVE_SCREENSHOT_TO_FILE = "save.screenshot.to.file";

  // Language Configuration
  public static final String APP_CODING_LANGUAGE = "app.coding.language";

  private ConfigurationKeys() {
    // Utility class
  }
}
