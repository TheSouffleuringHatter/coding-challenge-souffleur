package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void testFromStringWithValidNames() {
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("JAVA"));
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("java"));
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("Java"));

    assertEquals(CodingLanguage.PYTHON, CodingLanguage.fromString("PYTHON"));
    assertEquals(CodingLanguage.PYTHON, CodingLanguage.fromString("python"));
    assertEquals(CodingLanguage.PYTHON, CodingLanguage.fromString("Python"));

    assertEquals(CodingLanguage.CSHARP, CodingLanguage.fromString("CSHARP"));
    assertEquals(CodingLanguage.CSHARP, CodingLanguage.fromString("C#"));

    assertEquals(CodingLanguage.JAVASCRIPT, CodingLanguage.fromString("JAVASCRIPT"));
    assertEquals(CodingLanguage.JAVASCRIPT, CodingLanguage.fromString("JavaScript"));

    assertEquals(CodingLanguage.GOLANG, CodingLanguage.fromString("GOLANG"));
    assertEquals(CodingLanguage.GOLANG, CodingLanguage.fromString("Go"));
  }

  @Test
  void testFromStringWithInvalidNames() {
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("invalid"));
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString(""));
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString(null));
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("   "));
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
  void testFromStringCaseInsensitivity() {
    assertEquals(CodingLanguage.JAVASCRIPT, CodingLanguage.fromString("javascript"));
    assertEquals(CodingLanguage.JAVASCRIPT, CodingLanguage.fromString("JAVASCRIPT"));
    assertEquals(CodingLanguage.JAVASCRIPT, CodingLanguage.fromString("JaVaScRiPt"));
    assertEquals(CodingLanguage.CSHARP, CodingLanguage.fromString("csharp"));
    assertEquals(CodingLanguage.CSHARP, CodingLanguage.fromString("CSHARP"));
  }

  @Test
  void testFromStringWhitespaceHandling() {
    assertEquals(CodingLanguage.JAVA, CodingLanguage.fromString("  java  "));
    assertEquals(CodingLanguage.PYTHON, CodingLanguage.fromString("\tpython\t"));
    assertEquals(CodingLanguage.GOLANG, CodingLanguage.fromString(" go "));
  }

  @Test
  void testUniquePromptPaths() {
    var languages = CodingLanguage.values();
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
    var languages = CodingLanguage.values();
    var displayNames = new java.util.HashSet<String>();

    for (var language : languages) {
      var displayName = language.getDisplayName();
      assertNotNull(displayName);
      assertTrue(displayNames.add(displayName));
    }

    assertEquals(languages.length, displayNames.size());
  }
}
