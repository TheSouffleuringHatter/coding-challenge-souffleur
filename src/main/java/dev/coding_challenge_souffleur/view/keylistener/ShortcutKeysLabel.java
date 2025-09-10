package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import javafx.scene.control.Label;

public class ShortcutKeysLabel extends Label {

  public ShortcutKeysLabel() {
    super();
  }

  public ShortcutKeysLabel(Win32VK hideShowKey,
                           Win32VK moveUpKey,
                           Win32VK moveDownKey,
                           Win32VK moveLeftKey,
                           Win32VK moveRightKey,
                           Win32VK screenshotKey,
                           Win32VK runAnalysisKey,
                           Win32VK scrollUpKey,
                           Win32VK scrollDownKey) {
    super();
    var keysText =
        String.format(
            "🚫 %s | ↑ %s ↓ %s ← %s → %s | 📸 %s  🔍 %s | ⬆️%s ⬇️ %s",
            Character.toString(hideShowKey.code),
            moveUpKey,
            moveDownKey,
            moveLeftKey,
            moveRightKey,
            Character.toString(screenshotKey.code),
            Character.toString(runAnalysisKey.code),
            scrollUpKey,
            scrollDownKey);

    this.setText(keysText);
  }
}
