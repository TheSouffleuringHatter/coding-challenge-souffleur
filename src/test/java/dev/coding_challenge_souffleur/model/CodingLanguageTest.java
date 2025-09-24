package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import org.junit.jupiter.api.Test;

class CodingLanguageTest {

  @Test
  void testAllLanguagesHaveDisplayNames() {
    for (final var language : CodingLanguage.values()) {
      assertNotNull(language.getDisplayName());
      assertNotNull(language.getPromptFileName());
      assertNotNull(language.getPromptResourcePath());
    }
  }

  @Test
  void testPromptResourcePaths() {
    assertEquals("/prompts/java_prompt.txt", CodingLanguage.JAVA.getPromptResourcePath());
    assertEquals("/prompts/python_prompt.txt", CodingLanguage.PYTHON.getPromptResourcePath());
    assertEquals("/prompts/csharp_prompt.txt", CodingLanguage.CSHARP.getPromptResourcePath());
    assertEquals(
        "/prompts/javascript_prompt.txt", CodingLanguage.JAVASCRIPT.getPromptResourcePath());
    assertEquals("/prompts/golang_prompt.txt", CodingLanguage.GOLANG.getPromptResourcePath());
  }

  @Test
  void testDisplayNames() {
    assertEquals("Java", CodingLanguage.JAVA.getDisplayName());
    assertEquals("Python", CodingLanguage.PYTHON.getDisplayName());
    assertEquals("C#", CodingLanguage.CSHARP.getDisplayName());
    assertEquals("JavaScript", CodingLanguage.JAVASCRIPT.getDisplayName());
    assertEquals("Go", CodingLanguage.GOLANG.getDisplayName());
  }

  @Test
  void testToString() {
    assertEquals("Java", CodingLanguage.JAVA.toString());
    assertEquals("Python", CodingLanguage.PYTHON.toString());
    assertEquals("C#", CodingLanguage.CSHARP.toString());
    assertEquals("JavaScript", CodingLanguage.JAVASCRIPT.toString());
    assertEquals("Go", CodingLanguage.GOLANG.toString());
  }

  @Test
  void testEnumValueCount() {
    assertEquals(5, CodingLanguage.values().length);
  }

  @Test
  void testUniquePromptPaths() {
    var languages = CodingLanguage.values();
    var promptPaths = new HashSet<String>();

    for (var language : languages) {
      var path = language.getPromptResourcePath();
      assertNotNull(path);
      assertTrue(promptPaths.add(path));
    }

    assertEquals(languages.length, promptPaths.size());
  }

  @Test
  void testUniqueDisplayNames() {
    var languages = CodingLanguage.values();
    var displayNames = new HashSet<String>();

    for (var language : languages) {
      var displayName = language.getDisplayName();
      assertNotNull(displayName);
      assertTrue(displayNames.add(displayName));
    }

    assertEquals(languages.length, displayNames.size());
  }
}
