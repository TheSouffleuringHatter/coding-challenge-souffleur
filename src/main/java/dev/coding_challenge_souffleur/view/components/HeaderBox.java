package dev.coding_challenge_souffleur.view.components;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.model.AnthropicService;
import dev.coding_challenge_souffleur.model.CodingLanguage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderBox extends HBox {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeaderBox.class);
  private static final String HEADER_BOX_FXML = "HeaderBox.fxml";

  private final AnthropicService anthropicService;

  @FXML private Label shortcutModifierText;
  @FXML private Label shortcutKeysLabel;
  @FXML private ComboBox<CodingLanguage> codingLanguageSelector;
  @FXML private Button closeButton;

  public HeaderBox(
      final AnthropicService anthropicService,
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
      final Win32VK scrollDownKey,
      final Win32VK languagePreviousKey,
      final Win32VK languageNextKey) {
    loadFxml();
    this.anthropicService = anthropicService;

    shortcutModifierText.setText(
        modifierKeys.stream().map(Win32VK::toString).collect(Collectors.joining(" | ")));
    closeButton.setText("❌ (" + exitKeyCode + ")");

    var shortcutKeysText =
        String.format(
            "🚫%s | ↑%s ↓%s ←%s →%s | 📸%s 🔍%s | ⬆️%s ⬇️%s | 🔤%s %s",
            Character.toString(hideShowKey.code),
            Character.toString(moveUpKey.code),
            Character.toString(moveDownKey.code),
            Character.toString(moveLeftKey.code),
            Character.toString(moveRightKey.code),
            Character.toString(screenshotKey.code),
            Character.toString(runAnalysisKey.code),
            Character.toString(scrollUpKey.code),
            Character.toString(scrollDownKey.code),
            Character.toString(languagePreviousKey.code),
            Character.toString(languageNextKey.code));
    shortcutKeysLabel.setText(shortcutKeysText);

    codingLanguageSelector.setItems(FXCollections.observableArrayList(CodingLanguage.values()));
    codingLanguageSelector.setValue(CodingLanguage.JAVA);
  }

  public void cycleToNextCodingLanguage() {
    cycleCodingLanguageTo(CYCLE_DIRECTION.NEXT);
  }

  public void cycleToPreviousCodingLanguage() {
    cycleCodingLanguageTo(CYCLE_DIRECTION.PREVIOUS);
  }

  private void cycleCodingLanguageTo(CYCLE_DIRECTION direction) {
    var languages = CodingLanguage.values();
    var currentValue = codingLanguageSelector.getValue();
    var currentIndex = Arrays.asList(languages).indexOf(currentValue);
    int nextIndex;

    if (direction == CYCLE_DIRECTION.NEXT) {
      nextIndex = (currentIndex + 1) % languages.length;
    } else {
      nextIndex = (currentIndex - 1 + languages.length) % languages.length;
    }

    var codingLanguage = languages[nextIndex];
    LOGGER.debug("Cycle to {} language: {}", direction, codingLanguage);

    codingLanguageSelector.setValue(codingLanguage);
    anthropicService.setCodingLanguage(codingLanguage);
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

  private enum CYCLE_DIRECTION {
    NEXT,
    PREVIOUS;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}
