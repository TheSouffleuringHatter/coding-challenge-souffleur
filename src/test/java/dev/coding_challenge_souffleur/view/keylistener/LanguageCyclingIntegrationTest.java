package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.coding_challenge_souffleur.ConfigurationKeys;
import dev.coding_challenge_souffleur.model.CodingLanguageConfigurationService;
import dev.coding_challenge_souffleur.model.ProgrammingLanguage;
import org.junit.jupiter.api.Test;

class LanguageCyclingIntegrationTest {

  @Test
  void testLanguageKeyCommandsExist() {
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_CODING_LANGUAGE_NEXT,
        KeyCommand.LANGUAGE_NEXT.getKeyConfigProperty());
    assertEquals(
        ConfigurationKeys.APP_KEYBOARD_KEY_CODING_LANGUAGE_PREVIOUS,
        KeyCommand.LANGUAGE_PREVIOUS.getKeyConfigProperty());
  }

  @Test
  void testLanguageConfigurationServiceCycling() {
    var service = new CodingLanguageConfigurationService(ProgrammingLanguage.JAVA);
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }
}
