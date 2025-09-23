package dev.coding_challenge_souffleur.model;

/**
 * Enumeration of supported programming languages for code analysis and solution generation. Each
 * language has an associated display name and prompt file name for AI model configuration.
 */
public enum CodingLanguage {
  JAVA("Java", "java_prompt.txt"),
  PYTHON("Python", "python_prompt.txt"),
  CSHARP("C#", "csharp_prompt.txt"),
  JAVASCRIPT("JavaScript", "javascript_prompt.txt"),
  GOLANG("Go", "golang_prompt.txt");

  private final String displayName;
  private final String promptFileName;

  CodingLanguage(final String displayName, final String promptFileName) {
    this.displayName = displayName;
    this.promptFileName = promptFileName;
  }

  /**
   * Finds a CodingLanguage by its name (case-insensitive).
   *
   * @param name the name to search for
   * @return the matching CodingLanguage, or JAVA as default if not found
   */
  public static CodingLanguage fromString(final String name) {
    if (name == null || name.trim().isEmpty()) {
      return JAVA;
    }

    for (final var language : CodingLanguage.values()) {
      if (language.name().equalsIgnoreCase(name.trim())
          || language.getDisplayName().equalsIgnoreCase(name.trim())) {
        return language;
      }
    }

    return JAVA; // Default fallback
  }

  /**
   * Gets the human-readable display name for this programming language.
   *
   * @return the display name (e.g., "Java", "Python", "C#")
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Gets the filename of the language-specific prompt file.
   *
   * @return the prompt filename (e.g., "java_prompt.txt")
   */
  public String getPromptFileName() {
    return promptFileName;
  }

  /**
   * Gets the resource path for the language-specific prompt file.
   *
   * @return the full resource path for the prompt file
   */
  public String getPromptResourcePath() {
    return "/prompts/" + promptFileName;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
