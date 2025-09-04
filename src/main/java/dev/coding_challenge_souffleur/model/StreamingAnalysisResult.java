package dev.coding_challenge_souffleur.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a streaming analysis result that can be updated as sections are completed. Internally
 * stores section content in a map keyed by SolutionSection to reduce boilerplate and make it easy
 * to extend.
 */
public class StreamingAnalysisResult {

  private static final Logger LOGGER = LoggerFactory.getLogger(StreamingAnalysisResult.class);

  private final Map<SolutionSection, String> sections = new EnumMap<>(SolutionSection.class);

  /** Returns true if all required sections (excluding SOLUTION_TITLE) are present. */
  boolean isComplete() {
    return sections.containsKey(SolutionSection.PROBLEM_STATEMENT)
        && sections.containsKey(SolutionSection.SOLUTION_DESCRIPTION)
        && sections.containsKey(SolutionSection.EDGE_CASES)
        && sections.containsKey(SolutionSection.SOLUTION_CODE)
        && sections.containsKey(SolutionSection.TIME_COMPLEXITY)
        && sections.containsKey(SolutionSection.SPACE_COMPLEXITY);
  }

  public Optional<String> getSection(final SolutionSection solutionSection) {
    return Optional.ofNullable(sections.get(solutionSection));
  }

  void setSection(final SolutionSection solutionSection, final String value) {
    if (value == null) {
      throw new IllegalArgumentException("Value cannot be null when setting section " + solutionSection);
    }

    LOGGER.trace("Setting section {} to {}", solutionSection, value);
    sections.put(solutionSection, value);
  }
}
