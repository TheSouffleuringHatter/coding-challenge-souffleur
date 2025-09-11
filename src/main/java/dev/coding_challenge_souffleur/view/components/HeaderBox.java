package dev.coding_challenge_souffleur.view.components;

import com.sun.jna.platform.win32.Win32VK;
import java.io.IOException;
import java.util.List;
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
      final Win32VK exitKeyCode,
      final List<Win32VK> modifierKeys,
      final Win32VK hideShowKey,
      final Win32VK moveUpKey,
      final Win32VK moveDownKey,
      final Win32VK moveLeftKey,
      final Win32VK moveRightKey,
      final Win32VK screenshotKey,
      final Win32VK runAnalysisKey,
      final Win32VK scrollUpKey,
      final Win32VK scrollDownKey) {
    loadFxml();

    shortcutModifierText.setText(
        modifierKeys.stream()
            .map(Win32VK::toString)
            .collect(java.util.stream.Collectors.joining(" | ")));
    closeButton.setText("❌ (" + exitKeyCode + ")");

    var shortcutKeysText =
        String.format(
            "🚫%s | ↑%s ↓%s ←%s →%s | 📸%s 🔍%s | ⬆️%s ⬇️%s",
            Character.toString(hideShowKey.code),
            Character.toString(moveUpKey.code),
            Character.toString(moveDownKey.code),
            Character.toString(moveLeftKey.code),
            Character.toString(moveRightKey.code),
            Character.toString(screenshotKey.code),
            Character.toString(runAnalysisKey.code),
            Character.toString(scrollUpKey.code),
            Character.toString(scrollDownKey.code));
    shortcutKeysLabel.setText(shortcutKeysText);
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
