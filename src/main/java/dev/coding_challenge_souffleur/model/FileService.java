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
}
