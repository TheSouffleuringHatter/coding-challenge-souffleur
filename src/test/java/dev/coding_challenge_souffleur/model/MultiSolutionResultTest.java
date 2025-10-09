package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MultiSolutionResultTest {

  private MultiSolutionResult result;

  @BeforeEach
  void setUp() {
    result = new MultiSolutionResult();
  }

  @Nested
  class SolutionManagement {

    @Test
    void shouldStartWithNoSolutions() {
      assertEquals(0, result.getSolutionCount());
      assertFalse(result.hasAnySolutions());
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
    void shouldReturnUnmodifiableListOfSolutions() {
      var solution = new StreamingAnalysisResult();
      result.addSolution(solution);

      assertEquals(1, result.getSolutionCount());
    }
  }

  @Nested
  class SharedProblemStatement {

    @Test
    void shouldHandleSharedProblemStatement() {
      assertTrue(result.getSharedProblemStatement().isEmpty());

      result.setSharedProblemStatement("Test problem");
      assertTrue(result.getSharedProblemStatement().isPresent());
      assertEquals("Test problem", result.getSharedProblemStatement().get());

      result.setSharedProblemStatement(null);
      assertTrue(result.getSharedProblemStatement().isEmpty());
    }
  }

  @Nested
  class CompletionStatus {

    @Test
    void shouldNotBeCompleteWithNoSolutions() {
      assertFalse(result.isComplete());
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
      solution2.setSection(SolutionSection.SOLUTION_DESCRIPTION, "Description");
      solution2.setSection(SolutionSection.EDGE_CASES, "Edge cases");
      solution2.setSection(SolutionSection.SOLUTION_CODE, "Code");
      solution2.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");
      solution2.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");
      solution2.setSection(SolutionSection.PROBLEM_STATEMENT, "Problem");
      assertTrue(result.isComplete());
    }
  }

  private StreamingAnalysisResult createCompleteSolution() {
    var solution = new StreamingAnalysisResult();
    solution.setSection(SolutionSection.PROBLEM_STATEMENT, "Problem");
    solution.setSection(SolutionSection.SOLUTION_DESCRIPTION, "Description");
    solution.setSection(SolutionSection.EDGE_CASES, "Edge cases");
    solution.setSection(SolutionSection.SOLUTION_CODE, "Code");
    solution.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");
    solution.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");
    return solution;
  }
}
