package dev.coding_challenge_souffleur.model;

import dev.coding_challenge_souffleur.ConfigurationKeys;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CodingLanguageConfigurationService {

  private final ProgrammingLanguage configuredLanguage;
  private final List<Consumer<ProgrammingLanguage>> languageChangeListeners = new ArrayList<>();
  private ProgrammingLanguage currentLanguage;

  @Inject
  public CodingLanguageConfigurationService(
      @ConfigProperty(name = ConfigurationKeys.APP_DEFAULT_CODING_LANGUAGE)
          final ProgrammingLanguage languageConfig) {
    this.configuredLanguage = languageConfig;
    this.currentLanguage = this.configuredLanguage;
  }

  public ProgrammingLanguage getCurrentLanguage() {
    return currentLanguage;
  }

  public ProgrammingLanguage getConfiguredLanguage() {
    return configuredLanguage;
  }

  public void changeLanguage(final ProgrammingLanguage newLanguage) {
    if (newLanguage != null) {
      this.currentLanguage = newLanguage;
      notifyLanguageChangeListeners();
    }
  }

  public void resetToConfiguredLanguage() {
    this.currentLanguage = configuredLanguage;
  }

  public void cycleToNextLanguage() {
    var languages = ProgrammingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var nextIndex = (currentIndex + 1) % languages.length;
    this.currentLanguage = languages[nextIndex];
    notifyLanguageChangeListeners();
  }

  public void cycleToPreviousLanguage() {
    var languages = ProgrammingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var previousIndex = (currentIndex - 1 + languages.length) % languages.length;
    this.currentLanguage = languages[previousIndex];
    notifyLanguageChangeListeners();
  }

  public void addLanguageChangeListener(final Consumer<ProgrammingLanguage> listener) {
    if (listener != null) {
      languageChangeListeners.add(listener);
    }
  }

  public void removeLanguageChangeListener(final Consumer<ProgrammingLanguage> listener) {
    languageChangeListeners.remove(listener);
  }

  private void notifyLanguageChangeListeners() {
    for (var listener : languageChangeListeners) {
      try {
        listener.accept(currentLanguage);
      } catch (Exception e) {
        // Log but don't let listener exceptions break the service
        System.err.println("Error notifying language change listener: " + e.getMessage());
      }
    }
  }

  private int getCurrentLanguageIndex() {
    var languages = ProgrammingLanguage.values();
    for (var i = 0; i < languages.length; i++) {
      if (languages[i] == currentLanguage) {
        return i;
      }
    }
    return 0;
  }
}
