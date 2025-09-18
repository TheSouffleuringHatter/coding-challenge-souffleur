package dev.coding_challenge_souffleur.view.components;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.model.LanguageConfigurationService;
import dev.coding_challenge_souffleur.model.ProgrammingLanguage;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderBox extends HBox {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeaderBox.class);
  private static final String HEADER_BOX_FXML = "HeaderBox.fxml";

  @FXML private Label shortcutModifierText;
  @FXML private Label shortcutKeysLabel;
  @FXML private ComboBox<ProgrammingLanguage> languageSelector;
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
      final Win32VK scrollDownKey,
      final Win32VK languagePreviousKey,
      final Win32VK languageNextKey,
      final LanguageConfigurationService languageConfigurationService) {
    loadFxml();

    shortcutModifierText.setText(
        modifierKeys.stream()
            .map(Win32VK::toString)
            .collect(java.util.stream.Collectors.joining(" | ")));
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

    setupLanguageSelector(languageConfigurationService);

    // Register as listener for language changes via hotkeys
    languageConfigurationService.addLanguageChangeListener(this::onLanguageChanged);
  }

  private void setupLanguageSelector(final LanguageConfigurationService languageConfigurationService) {
    languageSelector.setItems(FXCollections.observableArrayList(ProgrammingLanguage.values()));
    languageSelector.setValue(languageConfigurationService.getCurrentLanguage());

    languageSelector.setCellFactory(listView -> new ListCell<>() {
      @Override
      protected void updateItem(final ProgrammingLanguage language, final boolean empty) {
        super.updateItem(language, empty);
        setText(empty || language == null ? null : language.getDisplayName());
      }
    });

    languageSelector.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(final ProgrammingLanguage language, final boolean empty) {
        super.updateItem(language, empty);
        setText(empty || language == null ? null : language.getDisplayName());
      }
    });

    languageSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue != oldValue) {
        languageConfigurationService.changeLanguage(newValue);
      }
    });

    languageSelector.setTooltip(new javafx.scene.control.Tooltip("Select programming language"));

    // Update selector when language changes via keyboard
    languageSelector.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null && newScene.getWindow() != null) {
        newScene.getWindow().focusedProperty().addListener((focusObs, wasFocused, isFocused) -> {
          if (isFocused) {
            updateLanguageSelector(languageConfigurationService);
          }
        });
      }
    });

    // Add multiple triggers to ensure UI updates when language changes
    languageSelector.setOnMouseEntered(e -> updateLanguageSelector(languageConfigurationService));
    languageSelector.setOnMouseMoved(e -> updateLanguageSelector(languageConfigurationService));

    // Also add a window focus listener that checks more frequently
    languageSelector.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
      if (isFocused) {
        updateLanguageSelector(languageConfigurationService);
      }
    });
  }

  private void updateLanguageSelector(final LanguageConfigurationService languageConfigurationService) {
    var currentLanguage = languageConfigurationService.getCurrentLanguage();
    if (languageSelector.getValue() != currentLanguage) {
      languageSelector.setValue(currentLanguage);
    }
  }

  private void onLanguageChanged(final ProgrammingLanguage newLanguage) {
    // Ensure UI updates happen on JavaFX Application Thread
    javafx.application.Platform.runLater(() -> {
      if (languageSelector.getValue() != newLanguage) {
        languageSelector.setValue(newLanguage);
      }
    });
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
