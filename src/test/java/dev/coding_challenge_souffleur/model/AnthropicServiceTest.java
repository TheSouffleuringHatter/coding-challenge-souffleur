package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.smallrye.config.inject.ConfigExtension;
import jakarta.inject.Inject;
import java.io.IOException;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@AddPackages(AnthropicService.class)
@AddExtensions(ConfigExtension.class)
class AnthropicServiceTest {

  private static byte[] testImage;
  @Inject private AnthropicService anthropicService;

  @BeforeAll
  static void setupTestImage() throws IOException {
    try (var inputStream =
        AnthropicServiceTest.class.getResourceAsStream(
            "/leetcode-screenshot-course-schedule-ii.png")) {
      if (inputStream == null) {
        throw new IllegalStateException("Test image resource not found");
      }
      testImage = inputStream.readAllBytes();
    }
  }

  @Test
  void testAnalyseMultiSolutionMock_Message_ReturnsAnalysisResult() {
    var future = anthropicService.analyseMultiSolutionMock(null);
    var multiSolutionResult = future.join();

    assertTrue(multiSolutionResult.hasAnySolutions());
    assertEquals(3, multiSolutionResult.getSolutionCount());
    assertTrue(
        multiSolutionResult
            .getSharedProblemStatement()
            .orElseThrow(() -> new AssertionError("Expected problem statement to be present"))
            .contains("Given four integer arrays"));

    var firstSolutionOpt = multiSolutionResult.getSolution(0);
    assertTrue(firstSolutionOpt.isPresent());
    var firstSolution = firstSolutionOpt.get();

    assertEquals("Brute Force Approach", firstSolution.getSolutionTitle());
    assertTrue(firstSolution.getSolutionDescription().contains("combinations of indices"));
    assertTrue(firstSolution.getEdgeCases().contains("Single element arrays"));
    assertTrue(firstSolution.getSolutionCode().contains("class Solution {"));
    assertTrue(firstSolution.getTimeComplexity().contains("O(n^4)"));
    assertTrue(firstSolution.getSpaceComplexity().contains("O(1)"));
  }

  @Test
  void testAnalyseMultiSolution_WithImageBytes_LiveSmoke_whenApiKeyPresent() {
    // Run twice because of internal async usage

    var future1 = anthropicService.analyseMultiSolution(testImage, null);
    var result1 = future1.join();
    assertTrue(result1.hasAnySolutions());
    assertTrue(result1.getSharedProblemStatement().isPresent());
    assertTrue(result1.isComplete());

    var future2 = anthropicService.analyseMultiSolution(testImage, null);
    var result2 = future2.join();
    assertTrue(result2.hasAnySolutions());
    assertTrue(result2.getSharedProblemStatement().isPresent());
    assertTrue(result2.isComplete());
  }
}
