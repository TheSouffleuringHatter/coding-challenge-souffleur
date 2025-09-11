package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class FileServiceTest {

  private final FileService fileService = new FileService();

  @Test
  void shouldLoadExistingResourceFile() throws IOException {
    String content = fileService.loadResourceFile("/junit-platform.properties");

    assertNotNull(content);
    assertFalse(content.isEmpty());
  }

  @Test
  void shouldThrowExceptionForNonExistentResource() {
    IOException exception =
        assertThrows(
            IOException.class, () -> fileService.loadResourceFile("/nonexistent/file.txt"));

    assertEquals("Resource not found: /nonexistent/file.txt", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullResourcePath() {
    assertThrows(Exception.class, () -> fileService.loadResourceFile(null));
  }

  @Test
  void shouldReturnContentForValidResourcePath() throws IOException {
    String content = fileService.loadResourceFile("/junit-platform.properties");

    assertNotNull(content);
    assertTrue(content.length() >= 0);
  }
}
