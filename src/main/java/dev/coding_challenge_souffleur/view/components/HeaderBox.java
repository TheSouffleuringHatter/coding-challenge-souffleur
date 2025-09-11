package dev.coding_challenge_souffleur.view.components;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.keylistener.MatchingModifier;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderBox extends HBox {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeaderBox.class);
  private static final String HEADER_BOX_FXML = "HeaderBox.fxml";

  @FXML private Label shortcutModifierText;
  @FXML private Label shortcutKeysLabel;
  @FXML private Button closeButton;

  public HeaderBox(
      Win32VK exitKeyCode,
      Win32VK hideShowKey,
      Win32VK moveUpKey,
      Win32VK moveDownKey,
      Win32VK moveLeftKey,
      Win32VK moveRightKey,
      Win32VK screenshotKey,
      Win32VK runAnalysisKey,
      Win32VK scrollUpKey,
      Win32VK scrollDownKey) {
    loadFxml();

    shortcutModifierText.setText(MatchingModifier.MATCHING_MODIFIER.toString());
    closeButton.setText("❌ (" + Character.toString(exitKeyCode.code) + ")");
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
    shortcutKeysLabel.setText(keysText);
  }

  private void loadFxml() {
    var fxmlLoader = new FXMLLoader(getClass().getResource(HEADER_BOX_FXML));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (IOException e) {
      LOGGER.warn("Failed to load FXML for HeaderBox", e);
      throw new RuntimeException("Failed to load HeaderBox FXML", e);
    }
  }
}
