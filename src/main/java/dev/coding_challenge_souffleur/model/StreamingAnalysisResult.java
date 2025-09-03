package dev.coding_challenge_souffleur.model;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a streaming analysis result that can be updated as sections are completed. Each
 * section can be in one of two states: - Not started/In progress: The section is not complete yet
 * (Optional.empty()) - Completed: The section is complete (Optional.of(content))
 *
 * <p>This is a simple data container without any listener mechanism.
 */
public class StreamingAnalysisResult {
  private static final Logger LOGGER = LoggerFactory.getLogger(StreamingAnalysisResult.class);
  private Optional<String> solutionTitle = Optional.empty();
  private Optional<String> problemStatement = Optional.empty();
  private Optional<String> solutionDescription = Optional.empty();
  private Optional<String> edgeCases = Optional.empty();
  private Optional<String> solutionCode = Optional.empty();
  private Optional<String> timeComplexity = Optional.empty();
  private Optional<String> spaceComplexity = Optional.empty();

  /** Returns true if all sections are complete. */
  public boolean isComplete() {
    return problemStatement.isPresent()
        && solutionDescription.isPresent()
        && edgeCases.isPresent()
        && solutionCode.isPresent()
        && timeComplexity.isPresent()
        && spaceComplexity.isPresent();
  }

  public Optional<String> getSolutionTitle() {
    return solutionTitle;
  }

  public void setSolutionTitle(final String solutionTitle) {
    LOGGER.trace("Setting solution title");
    this.solutionTitle = Optional.ofNullable(solutionTitle);
  }

  public Optional<String> getProblemStatement() {
    return problemStatement;
  }

  public void setProblemStatement(final String problemStatement) {
    LOGGER.trace("Setting problem statement");
    this.problemStatement = Optional.ofNullable(problemStatement);
  }

  public Optional<String> getSolutionDescription() {
    return solutionDescription;
  }

  public void setSolutionDescription(final String solutionDescription) {
    LOGGER.trace("Setting solution description");
    this.solutionDescription = Optional.ofNullable(solutionDescription);
  }

  public Optional<String> getEdgeCases() {
    return edgeCases;
  }

  public void setEdgeCases(final String edgeCases) {
    LOGGER.trace("Setting edge cases");
    this.edgeCases = Optional.ofNullable(edgeCases);
  }

  public Optional<String> getSolutionCode() {
    return solutionCode;
  }

  public void setSolutionCode(final String solutionCode) {
    LOGGER.trace("Setting solution code");
    this.solutionCode = Optional.ofNullable(solutionCode);
  }

  public Optional<String> getTimeComplexity() {
    return timeComplexity;
  }

  public void setTimeComplexity(final String timeComplexity) {
    LOGGER.trace("Setting time complexity");
    this.timeComplexity = Optional.ofNullable(timeComplexity);
  }

  public Optional<String> getSpaceComplexity() {
    return spaceComplexity;
  }

  public void setSpaceComplexity(final String spaceComplexity) {
    LOGGER.trace("Setting space complexity");
    this.spaceComplexity = Optional.ofNullable(spaceComplexity);
  }
}
