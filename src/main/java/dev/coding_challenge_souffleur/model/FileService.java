package dev.coding_challenge_souffleur.model;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FileService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

  /**
   * Loads a resource file from the classpath.
   *
   * @param resourcePath The path to the resource file (e.g., "/prompts/system_prompt.txt")
   * @return The content of the resource file as a string
   * @throws IOException If the resource file cannot be read or is not found
   */
  public String loadResourceFile(final String resourcePath) throws IOException {
    LOGGER.trace("Loading resource file: {}...", resourcePath);

    try (var inputStream = getClass().getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new IOException("Resource not found: " + resourcePath);
      }

      try (var reader =
          new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        LOGGER.debug("Loaded resource file: {}", resourcePath);
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }

  /**
   * Loads a resource file from the classpath, returning a default value if the file cannot be
   * loaded.
   *
   * @param resourcePath The path to the resource file
   * @param defaultValue The default value to return if loading fails
   * @return The content of the resource file, or the default value if loading fails
   */
  public String loadResourceFileOrDefault(final String resourcePath, final String defaultValue) {
    try {
      return loadResourceFile(resourcePath);
    } catch (final IOException e) {
      LOGGER.warn("Failed to load resource file: {}, using default value", resourcePath, e);
      return defaultValue;
    }
  }
}
