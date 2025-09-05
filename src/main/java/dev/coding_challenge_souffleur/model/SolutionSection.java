package dev.coding_challenge_souffleur.model;

import java.util.regex.Pattern;

public enum SolutionSection {
  PROBLEM_STATEMENT,
  SOLUTION_TITLE,
  SOLUTION_DESCRIPTION,
  EDGE_CASES,
  SOLUTION_CODE,
  TIME_COMPLEXITY,
  SPACE_COMPLEXITY;

  /**
   * Pattern that identifies the start of a solution block. Currently based on SOLUTION_TITLE. We
   * build it from the existing SOLUTION_TITLE header prefix to avoid maintaining a separate
   * constant.
   */
  static final Pattern SOLUTION_BOUNDARY_PATTERN_INSTANCE =
      Pattern.compile(SOLUTION_TITLE.headerPrefix(), Pattern.DOTALL);

  private static final String SECTION_END = "===SECTION_END===";

  // Precompiled per-enum constants
  private final String headerPrefix;
  private final Pattern completePattern;
  private final Pattern partialPattern;

  // Enum constructor to initialize constants
  SolutionSection() {
    this.headerPrefix = name() + ":";
    this.completePattern =
        Pattern.compile(this.headerPrefix + "(.*?)" + SECTION_END, Pattern.DOTALL);
    this.partialPattern =
        Pattern.compile(this.headerPrefix + "(.*?)(?=" + SECTION_END + "|$)", Pattern.DOTALL);
  }

  static boolean containsSolutionContent(final String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }

    for (final var section : values()) {
      if (section == PROBLEM_STATEMENT) {
        continue;
      }
      if (text.contains(section.headerPrefix())) {
        return true;
      }
    }

    return false;
  }

  /** Returns the standard header prefix for this section, e.g. "SOLUTION_TITLE:". */
  String headerPrefix() {
    return headerPrefix;
  }

  /**
   * Regex pattern that matches a complete section (terminated by SECTION_END) and captures its
   * content.
   */
  Pattern completePattern() {
    return completePattern;
  }

  /**
   * Regex pattern that matches a partial (or complete) section until SECTION_END or end-of-text and
   * captures its content.
   */
  Pattern partialPattern() {
    return partialPattern;
  }

  /**
   * Returns the section value from the given StreamingAnalysisResult.
   */
  public String getValue(final StreamingAnalysisResult result) {
    return switch (this) {
      case PROBLEM_STATEMENT -> result.getProblemStatement();
      case SOLUTION_TITLE -> result.getSolutionTitle();
      case SOLUTION_DESCRIPTION -> result.getSolutionDescription();
      case EDGE_CASES -> result.getEdgeCases();
      case SOLUTION_CODE -> result.getSolutionCode();
      case TIME_COMPLEXITY -> result.getTimeComplexity();
      case SPACE_COMPLEXITY -> result.getSpaceComplexity();
    };
  }
}
