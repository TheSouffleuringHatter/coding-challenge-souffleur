package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StreamingAnalysisResultTest {

  @Nested
  class CompletionStatus {

    @Test
    void shouldBeIncompleteWhenEmpty() {
      var result = new StreamingAnalysisResult();

      assertFalse(result.isComplete());
    }

    @Test
    void shouldBeCompleteWhenAllRequiredSectionsPresent() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.PROBLEM_STATEMENT, "Problem");
      result.setSection(SolutionSection.SOLUTION_DESCRIPTION, "Description");
      result.setSection(SolutionSection.EDGE_CASES, "Edge cases");
      result.setSection(SolutionSection.SOLUTION_CODE, "Code");
      result.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");
      result.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");

      assertTrue(result.isComplete());
    }

    @Test
    void shouldBeIncompleteWhenMissingSolutionDescription() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.PROBLEM_STATEMENT, "Problem");
      result.setSection(SolutionSection.EDGE_CASES, "Edge cases");
      result.setSection(SolutionSection.SOLUTION_CODE, "Code");
      result.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");
      result.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");

      assertFalse(result.isComplete());
    }

    @Test
    void shouldCompleteWithSolutionTitleOptional() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.PROBLEM_STATEMENT, "Problem");
      result.setSection(SolutionSection.SOLUTION_DESCRIPTION, "Description");
      result.setSection(SolutionSection.EDGE_CASES, "Edge cases");
      result.setSection(SolutionSection.SOLUTION_CODE, "Code");
      result.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");
      result.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");

      assertTrue(result.isComplete());
      assertNull(result.getSolutionTitle());
    }
  }

  @Nested
  class SectionGettersAndSetters {

    @Test
    void shouldSetAndGetProblemStatement() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.PROBLEM_STATEMENT, "Test problem");

      assertEquals("Test problem", result.getProblemStatement());
    }

    @Test
    void shouldSetAndGetSolutionTitle() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.SOLUTION_TITLE, "Test title");

      assertEquals("Test title", result.getSolutionTitle());
    }

    @Test
    void shouldSetAndGetSolutionDescription() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.SOLUTION_DESCRIPTION, "Test description");

      assertEquals("Test description", result.getSolutionDescription());
    }

    @Test
    void shouldSetAndGetEdgeCases() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.EDGE_CASES, "Test edge cases");

      assertEquals("Test edge cases", result.getEdgeCases());
    }

    @Test
    void shouldSetAndGetSolutionCode() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.SOLUTION_CODE, "Test code");

      assertEquals("Test code", result.getSolutionCode());
    }

    @Test
    void shouldSetAndGetTimeComplexity() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.TIME_COMPLEXITY, "O(n)");

      assertEquals("O(n)", result.getTimeComplexity());
    }

    @Test
    void shouldSetAndGetSpaceComplexity() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.SPACE_COMPLEXITY, "O(1)");

      assertEquals("O(1)", result.getSpaceComplexity());
    }

    @Test
    void shouldGetValueForSection() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.SOLUTION_TITLE, "Test title");

      assertEquals("Test title", result.getValueForSection(SolutionSection.SOLUTION_TITLE));
    }

    @Test
    void shouldReturnNullForUnsetSection() {
      var result = new StreamingAnalysisResult();

      assertNull(result.getProblemStatement());
      assertNull(result.getSolutionTitle());
      assertNull(result.getValueForSection(SolutionSection.SOLUTION_CODE));
    }

    @Test
    void shouldOverwriteExistingSection() {
      var result = new StreamingAnalysisResult();

      result.setSection(SolutionSection.PROBLEM_STATEMENT, "First value");
      result.setSection(SolutionSection.PROBLEM_STATEMENT, "Second value");

      assertEquals("Second value", result.getProblemStatement());
    }
  }

  @Nested
  class Validation {

    @Test
    void shouldThrowExceptionWhenSettingNullValue() {
      var result = new StreamingAnalysisResult();

      var exception =
          assertThrows(
              IllegalArgumentException.class,
              () -> result.setSection(SolutionSection.PROBLEM_STATEMENT, null));

      assertEquals(
          "Value cannot be null when setting section PROBLEM_STATEMENT", exception.getMessage());
    }
  }
}
