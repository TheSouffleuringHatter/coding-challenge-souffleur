package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LanguageConfigurationServiceTest {

  private LanguageConfigurationService service;

  @BeforeEach
  void setUp() {
    service = new LanguageConfigurationService(ProgrammingLanguage.JAVA);
  }

  @Test
  void testInitialConfiguration() {
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
    assertEquals(ProgrammingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testInitialConfigurationWithDifferentLanguage() {
    var pythonService = new LanguageConfigurationService(ProgrammingLanguage.PYTHON);
    assertEquals(ProgrammingLanguage.PYTHON, pythonService.getCurrentLanguage());
    assertEquals(ProgrammingLanguage.PYTHON, pythonService.getConfiguredLanguage());
  }

  @Test
  void testChangeLanguage() {
    service.changeLanguage(ProgrammingLanguage.PYTHON);
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());
    assertEquals(ProgrammingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testChangeLanguageToNull() {
    service.changeLanguage(null);
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testResetToConfiguredLanguage() {
    service.changeLanguage(ProgrammingLanguage.PYTHON);
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.resetToConfiguredLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCycleToNextLanguage() {
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCycleToPreviousLanguage() {
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCyclingFromDifferentStartingLanguage() {
    service.changeLanguage(ProgrammingLanguage.CSHARP);
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());
  }

  @Test
  void testCombinedCyclingOperations() {
    service.cycleToNextLanguage();
    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.resetToConfiguredLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testConfiguredLanguagePreservation() {
    var originalConfigured = service.getConfiguredLanguage();

    service.changeLanguage(ProgrammingLanguage.PYTHON);
    service.cycleToNextLanguage();
    service.cycleToPreviousLanguage();

    assertEquals(originalConfigured, service.getConfiguredLanguage());
  }

  @Test
  void testMultipleLanguageChanges() {
    service.changeLanguage(ProgrammingLanguage.PYTHON);
    assertEquals(ProgrammingLanguage.PYTHON, service.getCurrentLanguage());

    service.changeLanguage(ProgrammingLanguage.CSHARP);
    assertEquals(ProgrammingLanguage.CSHARP, service.getCurrentLanguage());

    service.changeLanguage(ProgrammingLanguage.JAVASCRIPT);
    assertEquals(ProgrammingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    assertEquals(ProgrammingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testCyclingWrapsCorrectlyAtBoundaries() {
    service.changeLanguage(ProgrammingLanguage.GOLANG);
    assertEquals(ProgrammingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    service.changeLanguage(ProgrammingLanguage.JAVA);
    service.cycleToPreviousLanguage();
    assertEquals(ProgrammingLanguage.GOLANG, service.getCurrentLanguage());
  }

  @Test
  void testConsecutiveCyclingOperations() {
    for (var i = 0; i < 10; i++) {
      service.cycleToNextLanguage();
    }
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());

    for (var i = 0; i < 15; i++) {
      service.cycleToPreviousLanguage();
    }
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testResetAfterMultipleOperations() {
    service.cycleToNextLanguage();
    service.changeLanguage(ProgrammingLanguage.CSHARP);
    service.cycleToPreviousLanguage();
    service.cycleToNextLanguage();

    service.resetToConfiguredLanguage();
    assertEquals(ProgrammingLanguage.JAVA, service.getCurrentLanguage());
    assertEquals(ProgrammingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testAllLanguagesAccessibleViaCycling() {
    var allLanguages = ProgrammingLanguage.values();
    var encounteredLanguages = new java.util.HashSet<ProgrammingLanguage>();

    for (var i = 0; i < allLanguages.length; i++) {
      encounteredLanguages.add(service.getCurrentLanguage());
      service.cycleToNextLanguage();
    }

    assertEquals(allLanguages.length, encounteredLanguages.size());
    for (var language : allLanguages) {
      assertTrue(encounteredLanguages.contains(language));
    }
  }
}
