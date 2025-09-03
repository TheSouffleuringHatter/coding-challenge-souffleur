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
  public boolean isComplete() {
    return getSection(SolutionSection.PROBLEM_STATEMENT).isPresent()
        && getSection(SolutionSection.SOLUTION_DESCRIPTION).isPresent()
        && getSection(SolutionSection.EDGE_CASES).isPresent()
        && getSection(SolutionSection.SOLUTION_CODE).isPresent()
        && getSection(SolutionSection.TIME_COMPLEXITY).isPresent()
        && getSection(SolutionSection.SPACE_COMPLEXITY).isPresent();
  }

  public Optional<String> getSection(final SolutionSection solutionSection) {
    return Optional.ofNullable(sections.get(solutionSection));
  }

  public void setSection(final SolutionSection solutionSection, final String value) {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Setting section {}", solutionSection);
    }
    if (value == null) {
      sections.remove(solutionSection);
    } else {
      sections.put(solutionSection, value);
    }
  }
}
