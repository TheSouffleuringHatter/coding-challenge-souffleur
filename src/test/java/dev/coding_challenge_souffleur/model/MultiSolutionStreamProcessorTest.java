package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultiSolutionStreamProcessorTest {

  private static final String SINGLE_SOLUTION_TEXT =
      """
      PROBLEM_STATEMENT:
      Test problem statement
      ===SECTION_END===

      SOLUTION_TITLE:
      Hash Map Approach
      ===SECTION_END===

      SOLUTION_DESCRIPTION:
      Test solution description
      ===SECTION_END===

      SOLUTION_CODE:
      def solution(): pass
      ===SECTION_END===

      TIME_COMPLEXITY:
      O(n)
      ===SECTION_END===

      SPACE_COMPLEXITY:
      O(1)
      ===SECTION_END===

      EDGE_CASES:
      Empty input
      ===SECTION_END===
      """;

  private static final String MULTI_SOLUTION_TEXT =
      """
      PROBLEM_STATEMENT:
      Test problem statement
      ===SECTION_END===

      SOLUTION_TITLE:
      Solution 1
      ===SECTION_END===

      SOLUTION_DESCRIPTION:
      First solution
      ===SECTION_END===

      SOLUTION_CODE:
      def solution1(): pass
      ===SECTION_END===

      TIME_COMPLEXITY:
      O(n)
      ===SECTION_END===

      SPACE_COMPLEXITY:
      O(1)
      ===SECTION_END===

      EDGE_CASES:
      Case 1
      ===SECTION_END===

      SOLUTION_TITLE:
      Solution 2
      ===SECTION_END===

      SOLUTION_DESCRIPTION:
      Second solution
      ===SECTION_END===

      SOLUTION_CODE:
      def solution2(): pass
      ===SECTION_END===

      TIME_COMPLEXITY:
      O(log n)
      ===SECTION_END===

      SPACE_COMPLEXITY:
      O(n)
      ===SECTION_END===

      EDGE_CASES:
      Case 2
      ===SECTION_END===
      """;

  private static final String TEXT_WITHOUT_SOLUTION_TITLES =
      """
      PROBLEM_STATEMENT:
      Test problem
      ===SECTION_END===

      SOLUTION_DESCRIPTION:
      Description without title
      ===SECTION_END===
      """;

  private static final String PROBLEM_STATEMENT_ONLY =
      """
      PROBLEM_STATEMENT:
      Progressive problem
      ===SECTION_END===
      """;

  private static final String SOLUTION_CONTINUATION =
      """
      SOLUTION_TITLE:
      Progressive Solution
      ===SECTION_END===

      SOLUTION_DESCRIPTION:
      Progressive description
      ===SECTION_END===
      """;

  private MultiSolutionStreamProcessor processor;
  private MultiSolutionResult result;

  @BeforeEach
  void setUp() {
    processor = new MultiSolutionStreamProcessor();
    result = new MultiSolutionResult();
  }

  @Test
  void shouldProcessSingleSolutionText() {
    var accumulatedText = new StringBuilder();
    processor.processStreamEvents(accumulatedText, result, null, SINGLE_SOLUTION_TEXT);

    assertEquals(1, result.getSolutionCount());
    assertTrue(result.getSharedProblemStatement().isPresent());
    assertEquals("Test problem statement", result.getSharedProblemStatement().get());

    var solution = result.getSolution(0).get();
    assertTrue(solution.getSolutionTitle().isPresent());
    assertEquals("Hash Map Approach", solution.getSolutionTitle().get());
    assertTrue(solution.getSolutionDescription().isPresent());
    assertEquals("Test solution description", solution.getSolutionDescription().get());
  }

  @Test
  void shouldProcessMultipleSolutionsText() {
    var accumulatedText = new StringBuilder();
    processor.processStreamEvents(accumulatedText, result, null, MULTI_SOLUTION_TEXT);

    assertEquals(2, result.getSolutionCount());
    assertTrue(result.getSharedProblemStatement().isPresent());

    var solution1 = result.getSolution(0).get();
    assertEquals("Solution 1", solution1.getSolutionTitle().get());
    assertEquals("First solution", solution1.getSolutionDescription().get());

    var solution2 = result.getSolution(1).get();
    assertEquals("Solution 2", solution2.getSolutionTitle().get());
    assertEquals("Second solution", solution2.getSolutionDescription().get());
  }

  @Test
  void shouldHandleEmptyText() {
    var accumulatedText = new StringBuilder();
    processor.processStreamEvents(accumulatedText, result, null, "");

    assertEquals(0, result.getSolutionCount());
    assertFalse(result.hasAnySolutions());
  }

  @Test
  void shouldHandleTextWithoutSolutionTitles() {
    var accumulatedText = new StringBuilder();
    processor.processStreamEvents(accumulatedText, result, null, TEXT_WITHOUT_SOLUTION_TITLES);

    // Should create one solution even without explicit SOLUTION_TITLE
    assertEquals(1, result.getSolutionCount());
    assertTrue(result.getSharedProblemStatement().isPresent());
    assertEquals("Test problem", result.getSharedProblemStatement().get());
  }

  @Test
  void shouldUpdateResultProgressively() {
    var accumulatedText = new StringBuilder();

    // First, add problem statement
    processor.processStreamEvents(accumulatedText, result, null, PROBLEM_STATEMENT_ONLY);

    assertTrue(result.getSharedProblemStatement().isPresent());
    assertEquals(0, result.getSolutionCount());

    // Then add first solution
    processor.processStreamEvents(accumulatedText, result, null, SOLUTION_CONTINUATION);

    assertEquals(1, result.getSolutionCount());
    var solution = result.getSolution(0).get();
    assertTrue(solution.getSolutionTitle().isPresent());
    assertTrue(solution.getSolutionDescription().isPresent());
    assertFalse(solution.getSolutionCode().isPresent()); // Not yet added
  }
}
