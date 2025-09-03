package dev.coding_challenge_souffleur.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a streaming analysis result that can be updated as sections are completed. Internally
 * stores section content in a map keyed by Section to reduce boilerplate and make it easy to
 * extend. Legacy getters/setters are retained as shims for backward compatibility.
 */
public class StreamingAnalysisResult {
  private static final Logger LOGGER = LoggerFactory.getLogger(StreamingAnalysisResult.class);

  private final Map<SolutionSection, String> sections = new EnumMap<>(SolutionSection.class);

  /** Returns true if all required sections (excluding SOLUTION_TITLE) are present. */
  public boolean isComplete() {
    return getSection(SolutionSection.PROBLEM_STATEMENT).isPresent()
        && getSection(SolutionSection.SOLUTION_TITLE).isPresent()
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

  // Backward-compatible shims
  public Optional<String> getSolutionTitle() {
    return getSection(SolutionSection.SOLUTION_TITLE);
  }

  public void setSolutionTitle(final String solutionTitle) {
    setSection(SolutionSection.SOLUTION_TITLE, solutionTitle);
  }

  public Optional<String> getProblemStatement() {
    return getSection(SolutionSection.PROBLEM_STATEMENT);
  }

  public void setProblemStatement(final String problemStatement) {
    setSection(SolutionSection.PROBLEM_STATEMENT, problemStatement);
  }

  public Optional<String> getSolutionDescription() {
    return getSection(SolutionSection.SOLUTION_DESCRIPTION);
  }

  public void setSolutionDescription(final String solutionDescription) {
    setSection(SolutionSection.SOLUTION_DESCRIPTION, solutionDescription);
  }

  public Optional<String> getEdgeCases() {
    return getSection(SolutionSection.EDGE_CASES);
  }

  public void setEdgeCases(final String edgeCases) {
    setSection(SolutionSection.EDGE_CASES, edgeCases);
  }

  public Optional<String> getSolutionCode() {
    return getSection(SolutionSection.SOLUTION_CODE);
  }

  public void setSolutionCode(final String solutionCode) {
    setSection(SolutionSection.SOLUTION_CODE, solutionCode);
  }

  public Optional<String> getTimeComplexity() {
    return getSection(SolutionSection.TIME_COMPLEXITY);
  }

  public void setTimeComplexity(final String timeComplexity) {
    setSection(SolutionSection.TIME_COMPLEXITY, timeComplexity);
  }

  public Optional<String> getSpaceComplexity() {
    return getSection(SolutionSection.SPACE_COMPLEXITY);
  }

  public void setSpaceComplexity(final String spaceComplexity) {
    setSection(SolutionSection.SPACE_COMPLEXITY, spaceComplexity);
  }
}
