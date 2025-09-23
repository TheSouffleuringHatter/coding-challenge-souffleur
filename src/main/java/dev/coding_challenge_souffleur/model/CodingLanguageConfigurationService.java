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

  private final CodingLanguage configuredLanguage;
  private final List<Consumer<CodingLanguage>> languageChangeListeners = new ArrayList<>();
  private CodingLanguage currentLanguage;

  @Inject
  public CodingLanguageConfigurationService(
      @ConfigProperty(name = ConfigurationKeys.APP_DEFAULT_CODING_LANGUAGE)
          final CodingLanguage languageConfig) {
    this.configuredLanguage = languageConfig;
    this.currentLanguage = this.configuredLanguage;
  }

  public CodingLanguage getCurrentLanguage() {
    return currentLanguage;
  }

  public CodingLanguage getConfiguredLanguage() {
    return configuredLanguage;
  }

  public void changeLanguage(final CodingLanguage newLanguage) {
    if (newLanguage != null) {
      this.currentLanguage = newLanguage;
      notifyLanguageChangeListeners();
    }
  }

  public void resetToConfiguredLanguage() {
    this.currentLanguage = configuredLanguage;
  }

  public void cycleToNextLanguage() {
    var languages = CodingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var nextIndex = (currentIndex + 1) % languages.length;
    this.currentLanguage = languages[nextIndex];
    notifyLanguageChangeListeners();
  }

  public void cycleToPreviousLanguage() {
    var languages = CodingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var previousIndex = (currentIndex - 1 + languages.length) % languages.length;
    this.currentLanguage = languages[previousIndex];
    notifyLanguageChangeListeners();
  }

  public void addLanguageChangeListener(final Consumer<CodingLanguage> listener) {
    if (listener != null) {
      languageChangeListeners.add(listener);
    }
  }

  public void removeLanguageChangeListener(final Consumer<CodingLanguage> listener) {
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
    var languages = CodingLanguage.values();
    for (var i = 0; i < languages.length; i++) {
      if (languages[i] == currentLanguage) {
        return i;
      }
    }
    return 0;
  }
}
