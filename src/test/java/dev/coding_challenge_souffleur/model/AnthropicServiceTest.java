package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
  @Inject private CodingLanguageConfigurationService codingLanguageConfigurationService;

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
    // Single real API call to verify actual API integration
    var future = anthropicService.analyseMultiSolution(testImage, null);
    var result = future.join();

    assertTrue(result.hasAnySolutions());
    assertTrue(result.getSharedProblemStatement().isPresent());
    assertTrue(result.isComplete());
  }

  @Test
  void testAnalyseMultiSolution_ConsecutiveStreaming_UsingMock() {
    // Test consecutive streaming capability using mock to avoid API costs
    // This tests the architecture's ability to handle multiple sequential calls

    var future1 = anthropicService.analyseMultiSolutionMock(null);
    var result1 = future1.join();
    assertTrue(result1.hasAnySolutions());
    assertTrue(result1.getSharedProblemStatement().isPresent());
    assertTrue(result1.isComplete());

    var future2 = anthropicService.analyseMultiSolutionMock(null);
    var result2 = future2.join();
    assertTrue(result2.hasAnySolutions());
    assertTrue(result2.getSharedProblemStatement().isPresent());
    assertTrue(result2.isComplete());
  }

  @Test
  void testBuildSystemMessageForDifferentLanguages() {
    // Test that system messages are built correctly for different languages
    var javaMessage = anthropicService.buildSystemMessage(CodingLanguage.JAVA);
    assertNotNull(javaMessage);
    assertTrue(javaMessage.contains("Java 21"));

    var pythonMessage = anthropicService.buildSystemMessage(CodingLanguage.PYTHON);
    assertNotNull(pythonMessage);
    assertTrue(pythonMessage.contains("Python 3.9+"));

    var csharpMessage = anthropicService.buildSystemMessage(CodingLanguage.CSHARP);
    assertNotNull(csharpMessage);
    assertTrue(csharpMessage.contains("C# 10+"));

    var jsMessage = anthropicService.buildSystemMessage(CodingLanguage.JAVASCRIPT);
    assertNotNull(jsMessage);
    assertTrue(jsMessage.contains("ES2020+"));

    var goMessage = anthropicService.buildSystemMessage(CodingLanguage.GOLANG);
    assertNotNull(goMessage);
    assertTrue(goMessage.contains("Go 1.18+"));
  }
}
