package dev.coding_challenge_souffleur.model;

enum SolutionSection {
  PROBLEM_STATEMENT,
  SOLUTION_TITLE,
  SOLUTION_DESCRIPTION,
  EDGE_CASES,
  SOLUTION_CODE,
  TIME_COMPLEXITY,
  SPACE_COMPLEXITY;

  static boolean containsSolutionContent(final String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }

    for (final var section : values()) {
      if (section == PROBLEM_STATEMENT) {
        continue;
      }
      if (text.contains(section.name() + ":")) {
        return true;
      }
    }

    return false;
  }
}
