package dev.coding_challenge_souffleur;

import static org.junit.jupiter.api.Assertions.*;

import dev.coding_challenge_souffleur.model.CodingLanguage;
import dev.coding_challenge_souffleur.model.CodingLanguageConfigurationService;
import org.junit.jupiter.api.Test;

/**
 * Simple functional test to verify the core language cycling functionality without the complexity
 * of JavaFX UI testing.
 */
class LanguageHotkeyFunctionalTest {

  @Test
  void testLanguageCyclingFunctionality() {
    var service = new CodingLanguageConfigurationService(CodingLanguage.JAVA);

    // Verify initial state
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    // Test cycling to next language
    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    // Test cycling to previous language
    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    // Test full cycle
    var allLanguages = CodingLanguage.values();
    for (var i = 0; i < allLanguages.length; i++) {
      service.cycleToNextLanguage();
    }
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }
}
