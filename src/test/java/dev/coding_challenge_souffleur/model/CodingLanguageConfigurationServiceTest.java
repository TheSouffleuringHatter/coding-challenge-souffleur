package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodingLanguageConfigurationServiceTest {

  private CodingLanguageConfigurationService service;

  @BeforeEach
  void setUp() {
    service = new CodingLanguageConfigurationService(CodingLanguage.JAVA);
  }

  @Test
  void testInitialConfiguration() {
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
    assertEquals(CodingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testInitialConfigurationWithDifferentLanguage() {
    var pythonService = new CodingLanguageConfigurationService(CodingLanguage.PYTHON);
    assertEquals(CodingLanguage.PYTHON, pythonService.getCurrentLanguage());
    assertEquals(CodingLanguage.PYTHON, pythonService.getConfiguredLanguage());
  }

  @Test
  void testChangeLanguage() {
    service.changeLanguage(CodingLanguage.PYTHON);
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());
    assertEquals(CodingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testChangeLanguageToNull() {
    service.changeLanguage(null);
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testResetToConfiguredLanguage() {
    service.changeLanguage(CodingLanguage.PYTHON);
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    service.resetToConfiguredLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCycleToNextLanguage() {
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCycleToPreviousLanguage() {
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testCyclingFromDifferentStartingLanguage() {
    service.changeLanguage(CodingLanguage.CSHARP);
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());
  }

  @Test
  void testCombinedCyclingOperations() {
    service.cycleToNextLanguage();
    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    service.resetToConfiguredLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testConfiguredLanguagePreservation() {
    var originalConfigured = service.getConfiguredLanguage();

    service.changeLanguage(CodingLanguage.PYTHON);
    service.cycleToNextLanguage();
    service.cycleToPreviousLanguage();

    assertEquals(originalConfigured, service.getConfiguredLanguage());
  }

  @Test
  void testMultipleLanguageChanges() {
    service.changeLanguage(CodingLanguage.PYTHON);
    assertEquals(CodingLanguage.PYTHON, service.getCurrentLanguage());

    service.changeLanguage(CodingLanguage.CSHARP);
    assertEquals(CodingLanguage.CSHARP, service.getCurrentLanguage());

    service.changeLanguage(CodingLanguage.JAVASCRIPT);
    assertEquals(CodingLanguage.JAVASCRIPT, service.getCurrentLanguage());

    assertEquals(CodingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testCyclingWrapsCorrectlyAtBoundaries() {
    service.changeLanguage(CodingLanguage.GOLANG);
    assertEquals(CodingLanguage.GOLANG, service.getCurrentLanguage());

    service.cycleToNextLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    service.changeLanguage(CodingLanguage.JAVA);
    service.cycleToPreviousLanguage();
    assertEquals(CodingLanguage.GOLANG, service.getCurrentLanguage());
  }

  @Test
  void testConsecutiveCyclingOperations() {
    for (var i = 0; i < 10; i++) {
      service.cycleToNextLanguage();
    }
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());

    for (var i = 0; i < 15; i++) {
      service.cycleToPreviousLanguage();
    }
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
  }

  @Test
  void testResetAfterMultipleOperations() {
    service.cycleToNextLanguage();
    service.changeLanguage(CodingLanguage.CSHARP);
    service.cycleToPreviousLanguage();
    service.cycleToNextLanguage();

    service.resetToConfiguredLanguage();
    assertEquals(CodingLanguage.JAVA, service.getCurrentLanguage());
    assertEquals(CodingLanguage.JAVA, service.getConfiguredLanguage());
  }

  @Test
  void testAllLanguagesAccessibleViaCycling() {
    var allLanguages = CodingLanguage.values();
    var encounteredLanguages = new java.util.HashSet<CodingLanguage>();

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
