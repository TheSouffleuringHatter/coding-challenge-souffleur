package dev.coding_challenge_souffleur.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for multiple streaming analysis results representing different solution approaches.
 * Each solution can be in progress or complete independently.
 */
public class MultiSolutionResult {
  private static final Logger LOGGER = LoggerFactory.getLogger(MultiSolutionResult.class);
  private final List<StreamingAnalysisResult> solutions = new ArrayList<>();
  private Optional<String> sharedProblemStatement = Optional.empty();

  public void addSolution(final StreamingAnalysisResult solution) {
    solutions.add(solution);
    LOGGER.trace("Added solution, total count: {}", solutions.size());
  }

  public List<StreamingAnalysisResult> getSolutions() {
    return Collections.unmodifiableList(solutions);
  }

  public int getSolutionCount() {
    return solutions.size();
  }

  public boolean hasAnySolutions() {
    return !solutions.isEmpty();
  }

  /** Returns true if all added solutions are complete. */
  public boolean isComplete() {
    return !solutions.isEmpty() && solutions.stream().allMatch(StreamingAnalysisResult::isComplete);
  }

  /** Returns the shared problem statement that applies to all solutions. */
  public Optional<String> getSharedProblemStatement() {
    return sharedProblemStatement;
  }

  public void setSharedProblemStatement(final String problemStatement) {
    LOGGER.trace("Setting shared problem statement");
    this.sharedProblemStatement = Optional.ofNullable(problemStatement);
  }

  /** Gets a specific solution by index, or empty if index is out of bounds. */
  public Optional<StreamingAnalysisResult> getSolution(final int index) {
    if (index >= 0 && index < solutions.size()) {
      return Optional.of(solutions.get(index));
    }
    return Optional.empty();
  }
}
