package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.smallrye.config.inject.ConfigExtension;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.stream.Stream;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.AddPackages;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

  private static Stream<Arguments> languageTestData() {
    return Stream.of(
      Arguments.of(CodingLanguage.JAVA, "Java 21"),
      Arguments.of(CodingLanguage.PYTHON, "Python 3.9+"),
      Arguments.of(CodingLanguage.CSHARP, "C# 10+"),
      Arguments.of(CodingLanguage.JAVASCRIPT, "ES2020+"),
      Arguments.of(CodingLanguage.GOLANG, "Go 1.18+")
    );
  }

  @ParameterizedTest
  @MethodSource("languageTestData")
  void testBuildSystemMessageForDifferentLanguages(CodingLanguage codingLanguage, String expectedContent) {
    anthropicService.setCodingLanguage(codingLanguage);

    var systemMessage = anthropicService.buildSystemMessage();
    assertNotNull(systemMessage);
    assertTrue(systemMessage.contains(expectedContent));
  }
}
