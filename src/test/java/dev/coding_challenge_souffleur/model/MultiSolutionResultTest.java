package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultiSolutionResultTest {

  private MultiSolutionResult result;

  @BeforeEach
  void setUp() {
    result = new MultiSolutionResult();
  }

  @Test
  void shouldStartWithNoSolutions() {
    assertEquals(0, result.getSolutionCount());
    assertFalse(result.hasAnySolutions());
  }

  @Test
  void shouldNotBeCompleteWithNoSolutions() {
    assertFalse(result.isComplete());
  }

  @Test
  void shouldAddSolutionsAndIncreaseCount() {
    var solution1 = new StreamingAnalysisResult();
    var solution2 = new StreamingAnalysisResult();

    result.addSolution(solution1);
    assertEquals(1, result.getSolutionCount());
    assertTrue(result.hasAnySolutions());

    result.addSolution(solution2);
    assertEquals(2, result.getSolutionCount());
  }

  @Test
  void shouldReturnSolutionByIndex() {
    var solution = new StreamingAnalysisResult();
    result.addSolution(solution);

    assertTrue(result.getSolution(0).isPresent());
    assertSame(solution, result.getSolution(0).get());
    assertTrue(result.getSolution(1).isEmpty());
    assertTrue(result.getSolution(-1).isEmpty());
  }

  @Test
  void shouldHandleSharedProblemStatement() {
    assertTrue(result.getSharedProblemStatement().isEmpty());

    result.setSharedProblemStatement("Test problem");
    assertTrue(result.getSharedProblemStatement().isPresent());
    assertEquals("Test problem", result.getSharedProblemStatement().get());

    result.setSharedProblemStatement(null);
    assertTrue(result.getSharedProblemStatement().isEmpty());
  }

  @Test
  void shouldBeCompleteWhenAllSolutionsAreComplete() {
    // Empty result is not complete
    assertFalse(result.isComplete());

    var solution1 = createCompleteSolution();
    result.addSolution(solution1);
    assertTrue(result.isComplete());

    var solution2 = new StreamingAnalysisResult(); // Incomplete solution
    result.addSolution(solution2);
    assertFalse(result.isComplete());

    // Make second solution complete
    solution2.setSolutionDescription("Description");
    solution2.setEdgeCases("Edge cases");
    solution2.setSolutionCode("Code");
    solution2.setTimeComplexity("O(n)");
    solution2.setSpaceComplexity("O(1)");
    solution2.setProblemStatement("Problem");
    assertTrue(result.isComplete());
  }

  @Test
  void shouldReturnUnmodifiableListOfSolutions() {
    var solution = new StreamingAnalysisResult();
    result.addSolution(solution);

    var solutions = result.getSolutions();
    assertEquals(1, solutions.size());

    // Should throw UnsupportedOperationException when trying to modify
    assertThrows(UnsupportedOperationException.class, () -> solutions.clear());
  }

  private StreamingAnalysisResult createCompleteSolution() {
    var solution = new StreamingAnalysisResult();
    solution.setProblemStatement("Problem");
    solution.setSolutionDescription("Description");
    solution.setEdgeCases("Edge cases");
    solution.setSolutionCode("Code");
    solution.setTimeComplexity("O(n)");
    solution.setSpaceComplexity("O(1)");
    return solution;
  }
}