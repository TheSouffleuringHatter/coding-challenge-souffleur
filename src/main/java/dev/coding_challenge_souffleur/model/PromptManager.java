package dev.coding_challenge_souffleur.model;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;

@ApplicationScoped
class PromptManager {

  private final String systemMessage;
  private final String userMessage;

  @Inject
  PromptManager(final FileService fileService) {
    try {
      this.systemMessage =
          fileService.loadResourceFile("/prompts/system_prompt.txt")
              + fileService.loadResourceFile("/prompts/text_response_prompt.txt")
              + fileService.loadResourceFile("/prompts/java_prompt.txt")
              + fileService.loadResourceFile("/prompts/assistant_message.txt");
      this.userMessage = fileService.loadResourceFile("/prompts/user_message.txt");
    } catch (final IOException e) {
      throw new RuntimeException("Failed to load prompt files", e);
    }
  }

  String getSystemMessage() {
    return systemMessage;
  }

  String getUserMessage() {
    return userMessage;
  }
}
