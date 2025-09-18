package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProgrammingLanguageTest {

  @Test
  void testAllLanguagesHaveDisplayNames() {
    for (final var language : ProgrammingLanguage.values()) {
      assertNotNull(language.getDisplayName());
      assertNotNull(language.getPromptFileName());
      assertNotNull(language.getPromptResourcePath());
    }
  }

  @Test
  void testFromStringWithValidNames() {
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("JAVA"));
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("java"));
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("Java"));

    assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromString("PYTHON"));
    assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromString("python"));
    assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromString("Python"));

    assertEquals(ProgrammingLanguage.CSHARP, ProgrammingLanguage.fromString("CSHARP"));
    assertEquals(ProgrammingLanguage.CSHARP, ProgrammingLanguage.fromString("C#"));

    assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromString("JAVASCRIPT"));
    assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromString("JavaScript"));

    assertEquals(ProgrammingLanguage.GOLANG, ProgrammingLanguage.fromString("GOLANG"));
    assertEquals(ProgrammingLanguage.GOLANG, ProgrammingLanguage.fromString("Go"));
  }

  @Test
  void testFromStringWithInvalidNames() {
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("invalid"));
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString(""));
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString(null));
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("   "));
  }

  @Test
  void testPromptResourcePaths() {
    assertEquals("/prompts/java_prompt.txt", ProgrammingLanguage.JAVA.getPromptResourcePath());
    assertEquals("/prompts/python_prompt.txt", ProgrammingLanguage.PYTHON.getPromptResourcePath());
    assertEquals("/prompts/csharp_prompt.txt", ProgrammingLanguage.CSHARP.getPromptResourcePath());
    assertEquals(
        "/prompts/javascript_prompt.txt", ProgrammingLanguage.JAVASCRIPT.getPromptResourcePath());
    assertEquals("/prompts/golang_prompt.txt", ProgrammingLanguage.GOLANG.getPromptResourcePath());
  }

  @Test
  void testDisplayNames() {
    assertEquals("Java", ProgrammingLanguage.JAVA.getDisplayName());
    assertEquals("Python", ProgrammingLanguage.PYTHON.getDisplayName());
    assertEquals("C#", ProgrammingLanguage.CSHARP.getDisplayName());
    assertEquals("JavaScript", ProgrammingLanguage.JAVASCRIPT.getDisplayName());
    assertEquals("Go", ProgrammingLanguage.GOLANG.getDisplayName());
  }

  @Test
  void testToString() {
    assertEquals("Java", ProgrammingLanguage.JAVA.toString());
    assertEquals("Python", ProgrammingLanguage.PYTHON.toString());
    assertEquals("C#", ProgrammingLanguage.CSHARP.toString());
    assertEquals("JavaScript", ProgrammingLanguage.JAVASCRIPT.toString());
    assertEquals("Go", ProgrammingLanguage.GOLANG.toString());
  }

  @Test
  void testEnumValueCount() {
    assertEquals(5, ProgrammingLanguage.values().length);
  }

  @Test
  void testFromStringCaseInsensitivity() {
    assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromString("javascript"));
    assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromString("JAVASCRIPT"));
    assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromString("JaVaScRiPt"));
    assertEquals(ProgrammingLanguage.CSHARP, ProgrammingLanguage.fromString("csharp"));
    assertEquals(ProgrammingLanguage.CSHARP, ProgrammingLanguage.fromString("CSHARP"));
  }

  @Test
  void testFromStringWhitespaceHandling() {
    assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromString("  java  "));
    assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromString("\tpython\t"));
    assertEquals(ProgrammingLanguage.GOLANG, ProgrammingLanguage.fromString(" go "));
  }

  @Test
  void testUniquePromptPaths() {
    var languages = ProgrammingLanguage.values();
    var promptPaths = new java.util.HashSet<String>();

    for (var language : languages) {
      var path = language.getPromptResourcePath();
      assertNotNull(path);
      assertTrue(promptPaths.add(path));
    }

    assertEquals(languages.length, promptPaths.size());
  }

  @Test
  void testUniqueDisplayNames() {
    var languages = ProgrammingLanguage.values();
    var displayNames = new java.util.HashSet<String>();

    for (var language : languages) {
      var displayName = language.getDisplayName();
      assertNotNull(displayName);
      assertTrue(displayNames.add(displayName));
    }

    assertEquals(languages.length, displayNames.size());
  }
}
