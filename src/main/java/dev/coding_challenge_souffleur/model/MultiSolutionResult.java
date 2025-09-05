package dev.coding_challenge_souffleur.model;

import java.util.ArrayList;
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
  private String sharedProblemStatement;

  public int getSolutionCount() {
    return solutions.size();
  }

  /** Returns true if all added solutions are complete. */
  public boolean isComplete() {
    if (solutions.isEmpty()) {
      return false;
    }

    // If we have a shared problem statement, treat solutions as complete when all
    // per-solution sections (except problem statement) are present.
    if (sharedProblemStatement != null) {
      return solutions.stream()
          .allMatch(
              s ->
                  s.getSolutionDescription() != null
                      && s.getEdgeCases() != null
                      && s.getSolutionCode() != null
                      && s.getTimeComplexity() != null
                      && s.getSpaceComplexity() != null);
    }

    // Fallback: require each solution to be fully complete on its own
    return solutions.stream().allMatch(StreamingAnalysisResult::isComplete);
  }

  /** Returns the shared problem statement that applies to all solutions. */
  public Optional<String> getSharedProblemStatement() {
    return Optional.ofNullable(sharedProblemStatement);
  }

  void setSharedProblemStatement(final String problemStatement) {
    LOGGER.trace("Setting shared problem statement to {}", problemStatement);
    this.sharedProblemStatement = problemStatement;
  }

  /** Gets a specific solution by index, or empty if index is out of bounds. */
  public Optional<StreamingAnalysisResult> getSolution(final int index) {
    if (index >= 0 && index < solutions.size()) {
      return Optional.of(solutions.get(index));
    }

    return Optional.empty();
  }

  void addSolution(final StreamingAnalysisResult solution) {
    solutions.add(solution);
    LOGGER.trace("Added solution, total count: {}", solutions.size());
  }

  boolean hasAnySolutions() {
    return !solutions.isEmpty();
  }
}
