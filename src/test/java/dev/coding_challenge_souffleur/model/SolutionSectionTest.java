package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class SolutionSectionTest {

  @Test
  void shouldHaveCorrectHeaderPrefixes() {
    assertEquals(
        SolutionSection.PROBLEM_STATEMENT.name() + ":",
        SolutionSection.PROBLEM_STATEMENT.headerPrefix());
    assertEquals(
        SolutionSection.SOLUTION_TITLE.name() + ":", SolutionSection.SOLUTION_TITLE.headerPrefix());
    assertEquals(
        SolutionSection.SOLUTION_DESCRIPTION.name() + ":",
        SolutionSection.SOLUTION_DESCRIPTION.headerPrefix());
    assertEquals(
        SolutionSection.EDGE_CASES.name() + ":", SolutionSection.EDGE_CASES.headerPrefix());
    assertEquals(
        SolutionSection.SOLUTION_CODE.name() + ":", SolutionSection.SOLUTION_CODE.headerPrefix());
    assertEquals(
        SolutionSection.TIME_COMPLEXITY.name() + ":",
        SolutionSection.TIME_COMPLEXITY.headerPrefix());
    assertEquals(
        SolutionSection.SPACE_COMPLEXITY.name() + ":",
        SolutionSection.SPACE_COMPLEXITY.headerPrefix());
  }

  @Test
  void shouldReturnValidCompletePattern() {
    Pattern pattern = SolutionSection.SOLUTION_TITLE.completePattern();

    assertNotNull(pattern);
    assertTrue(
        pattern
            .matcher(
                SolutionSection.SOLUTION_TITLE.headerPrefix()
                    + "Some content"
                    + SolutionSection.SECTION_END)
            .matches());
    assertFalse(
        pattern.matcher(SolutionSection.SOLUTION_TITLE.headerPrefix() + "Some content").matches());
  }

  @Test
  void shouldReturnValidPartialPattern() {
    Pattern pattern = SolutionSection.SOLUTION_TITLE.partialPattern();

    assertNotNull(pattern);
    assertTrue(
        pattern
            .matcher(
                SolutionSection.SOLUTION_TITLE.headerPrefix()
                    + "Some content"
                    + SolutionSection.SECTION_END)
            .find());
    assertTrue(
        pattern.matcher(SolutionSection.SOLUTION_TITLE.headerPrefix() + "Some content").find());
  }

  @Test
  void shouldDetectSolutionContentWhenPresent() {
    String textWithSolution =
        "Some text " + SolutionSection.SOLUTION_TITLE.headerPrefix() + " My solution title";

    assertTrue(SolutionSection.containsSolutionContent(textWithSolution));
  }

  @Test
  void shouldDetectSolutionContentForMultipleSections() {
    String textWithSolution =
        SolutionSection.SOLUTION_DESCRIPTION.headerPrefix()
            + " Description here "
            + SolutionSection.EDGE_CASES.headerPrefix()
            + " Some edge cases";

    assertTrue(SolutionSection.containsSolutionContent(textWithSolution));
  }

  @Test
  void shouldNotDetectSolutionContentWhenOnlyProblemStatementPresent() {
    String textWithoutSolution =
        "Some text " + SolutionSection.PROBLEM_STATEMENT.headerPrefix() + " Problem description";

    assertFalse(SolutionSection.containsSolutionContent(textWithoutSolution));
  }

  @Test
  void shouldNotDetectSolutionContentWhenNotPresent() {
    String textWithoutSolution = "Some random text without any solution sections";

    assertFalse(SolutionSection.containsSolutionContent(textWithoutSolution));
  }

  @Test
  void shouldReturnFalseForNullText() {
    assertFalse(SolutionSection.containsSolutionContent(null));
  }

  @Test
  void shouldReturnFalseForEmptyText() {
    assertFalse(SolutionSection.containsSolutionContent(""));
  }

  @Test
  void shouldHaveSolutionBoundaryPattern() {
    Pattern pattern = SolutionSection.SOLUTION_BOUNDARY_PATTERN_INSTANCE;

    assertNotNull(pattern);
    assertTrue(pattern.matcher(SolutionSection.SOLUTION_TITLE.headerPrefix()).find());
  }

  @Test
  void shouldMatchCompletePatternWithSectionEnd() {
    String text =
        SolutionSection.SOLUTION_TITLE.headerPrefix()
            + "My awesome solution"
            + SolutionSection.SECTION_END;
    Pattern pattern = SolutionSection.SOLUTION_TITLE.completePattern();

    assertTrue(pattern.matcher(text).matches());
  }

  @Test
  void shouldCaptureContentFromCompletePattern() {
    String text =
        SolutionSection.SOLUTION_TITLE.headerPrefix()
            + "My awesome solution"
            + SolutionSection.SECTION_END;
    Pattern pattern = SolutionSection.SOLUTION_TITLE.completePattern();
    var matcher = pattern.matcher(text);

    assertTrue(matcher.matches());
    assertEquals("My awesome solution", matcher.group(1));
  }

  @Test
  void shouldMatchPartialPatternWithoutSectionEnd() {
    String text = SolutionSection.SOLUTION_TITLE.headerPrefix() + "My awesome solution";
    Pattern pattern = SolutionSection.SOLUTION_TITLE.partialPattern();

    assertTrue(pattern.matcher(text).find());
  }

  @Test
  void shouldCaptureContentFromPartialPattern() {
    String text = SolutionSection.SOLUTION_TITLE.headerPrefix() + "My awesome solution";
    Pattern pattern = SolutionSection.SOLUTION_TITLE.partialPattern();
    var matcher = pattern.matcher(text);

    assertTrue(matcher.find());
    assertEquals("My awesome solution", matcher.group(1));
  }
}
