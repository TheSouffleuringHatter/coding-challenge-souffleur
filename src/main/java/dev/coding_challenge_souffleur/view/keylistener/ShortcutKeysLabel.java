package dev.coding_challenge_souffleur.view.keylistener;

import javafx.scene.control.Label;

public class ShortcutKeysLabel extends Label {

  public ShortcutKeysLabel() {
    var keysText =
        String.format(
            "🚫 %s | ↑ %s ↓ %s ← %s → %s | 📸 %s  🔍 %s | ⬆️%s ⬇️ %s",
            Character.toString(HideShow.HIDE_SHOW_KEY_CODE.code),
            MoveStageUp.MOVE_UP_KEY_CODE,
            MoveStageDown.MOVE_DOWN_KEY_CODE,
            MoveStageLeft.MOVE_LEFT_KEY_CODE,
            MoveStageRight.MOVE_RIGHT_KEY_CODE,
            Character.toString(TakeScreenshot.SCREENSHOT_KEY_CODE.code),
            Character.toString(RunAnalysis.ANALYSE_KEY_CODE.code),
            ScrollContentUp.SCROLL_UP_KEY_CODE,
            ScrollContentDown.SCROLL_DOWN_KEY_CODE);

    this.setText(keysText);
  }
}
