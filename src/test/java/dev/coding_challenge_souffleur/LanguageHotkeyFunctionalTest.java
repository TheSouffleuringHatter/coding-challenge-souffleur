package dev.coding_challenge_souffleur;

import static org.junit.jupiter.api.Assertions.*;

import dev.coding_challenge_souffleur.model.CodingLanguageConfigurationService;
import dev.coding_challenge_souffleur.model.ProgrammingLanguage;
import org.junit.jupiter.api.Test;

/**
 * Simple functional test to verify the core language cycling functionality without the complexity
 * of JavaFX UI testing.
 */
class LanguageHotkeyFunctionalTest {

  @Test
  void testLanguageCyclingFunctionality() {
    var service = new CodingLanguageConfigurationService(ProgrammingLanguage.JAVA);

    // Verify initial state
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    // Test cycling to next language
    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    // Test cycling to previous language
    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    // Test full cycle
    var allLanguages = ProgrammingLanguage.values();
    for (var i = 0; i < allLanguages.length; i++) {
      service.cycleToNextLanguage();
    }
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }
}
