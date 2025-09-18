package dev.coding_challenge_souffleur.view.components;

import dev.coding_challenge_souffleur.model.ProgrammingLanguage;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom JavaFX component for selecting programming languages. Provides a ComboBox with all
 * supported programming languages.
 */
public class LanguageSelector extends ComboBox<ProgrammingLanguage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LanguageSelector.class);

  private Consumer<ProgrammingLanguage> onLanguageChanged;

  public LanguageSelector() {
    setupComponent();
  }

  /**
   * Creates a LanguageSelector with an initial selected language.
   *
   * @param initialLanguage the initially selected language
   */
  public LanguageSelector(final ProgrammingLanguage initialLanguage) {
    this();
    setValue(initialLanguage);
  }

  private void setupComponent() {
    this.getStyleClass().add("language-selector");
    this.setId("languageSelector");

    // Populate with all available programming languages
    this.setItems(FXCollections.observableArrayList(ProgrammingLanguage.values()));

    // Set default value to Java
    this.setValue(ProgrammingLanguage.JAVA);

    // Custom cell factory to display language names properly
    this.setCellFactory(
        listView ->
            new ListCell<>() {
              @Override
              protected void updateItem(final ProgrammingLanguage language, final boolean empty) {
                super.updateItem(language, empty);
                if (empty || language == null) {
                  setText(null);
                } else {
                  setText(language.getDisplayName());
                }
              }
            });

    // Custom button cell to display selected language properly
    this.setButtonCell(
        new ListCell<>() {
          @Override
          protected void updateItem(final ProgrammingLanguage language, final boolean empty) {
            super.updateItem(language, empty);
            if (empty || language == null) {
              setText(null);
            } else {
              setText(language.getDisplayName());
            }
          }
        });

    // Handle selection changes
    this.getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null && newValue != oldValue) {
                LOGGER.debug("Language selection changed from {} to {}", oldValue, newValue);
                if (onLanguageChanged != null) {
                  onLanguageChanged.accept(newValue);
                }
              }
            });

    // Set tooltip
    this.setTooltip(
        new javafx.scene.control.Tooltip("Select programming language for code analysis"));
  }

  /**
   * Sets the callback to be invoked when the language selection changes.
   *
   * @param onLanguageChanged callback function that receives the newly selected language
   */
  public void setOnLanguageChanged(final Consumer<ProgrammingLanguage> onLanguageChanged) {
    this.onLanguageChanged = onLanguageChanged;
  }

  /**
   * Gets the currently selected programming language.
   *
   * @return the selected programming language, or JAVA if none selected
   */
  public ProgrammingLanguage getSelectedLanguage() {
    var selected = getValue();
    return selected != null ? selected : ProgrammingLanguage.JAVA;
  }

  /**
   * Sets the selected programming language.
   *
   * @param language the language to select
   */
  public void setSelectedLanguage(final ProgrammingLanguage language) {
    if (language != null) {
      setValue(language);
    }
  }
}
