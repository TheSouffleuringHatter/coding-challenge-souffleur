package dev.coding_challenge_souffleur.model;

import dev.coding_challenge_souffleur.ConfigurationKeys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CodingLanguageConfigurationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CodingLanguageConfigurationService.class);

  private final List<Consumer<CodingLanguage>> languageChangeListeners = new ArrayList<>();

  private CodingLanguage currentLanguage;

  @Inject
  public CodingLanguageConfigurationService(
      @ConfigProperty(name = ConfigurationKeys.APP_DEFAULT_CODING_LANGUAGE)
          final CodingLanguage languageConfig) {
    this.currentLanguage = languageConfig;
  }

  @PostConstruct
  void initializeLanguageConsumers() {
    // Notify all listeners with the initial language
    notifyLanguageChangeListeners();
  }

  @Produces
  public CodingLanguage getCurrentLanguage() {
    return currentLanguage;
  }

  public void changeLanguage(final CodingLanguage newLanguage) {
    if (newLanguage != null) {
      LOGGER.trace("Changing coding language to {}", newLanguage);
      this.currentLanguage = newLanguage;
      notifyLanguageChangeListeners();
    }
  }

  public void cycleToNextLanguage() {
    var languages = CodingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var nextIndex = (currentIndex + 1) % languages.length;
    this.currentLanguage = languages[nextIndex];
    LOGGER.debug("Cycle to next language: {}", currentLanguage);

    notifyLanguageChangeListeners();
  }

  public void cycleToPreviousLanguage() {
    var languages = CodingLanguage.values();
    var currentIndex = getCurrentLanguageIndex();
    var previousIndex = (currentIndex - 1 + languages.length) % languages.length;
    this.currentLanguage = languages[previousIndex];
    LOGGER.debug("Cycle to previous language: {}", currentLanguage);

    notifyLanguageChangeListeners();
  }

  public void addLanguageChangeListener(final Consumer<CodingLanguage> listener) {
    if (listener != null) {
      languageChangeListeners.add(listener);
    }
  }

  private void notifyLanguageChangeListeners() {
    for (var listener : languageChangeListeners) {
      try {
        listener.accept(currentLanguage);
      } catch (Exception e) {
        LOGGER.warn("Error notifying language change listener", e);
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
