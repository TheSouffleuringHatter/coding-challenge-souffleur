package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.coding_challenge_souffleur.view.ViewController;
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
  @Inject private FileService fileService;

  @BeforeAll
  static void setupTestImage() throws IOException {
    var inputStream =
        AnthropicServiceTest.class.getResourceAsStream(
            "/leetcode-screenshot-course-schedule-ii.png");
    testImage = inputStream.readAllBytes();
  }

  @Test
  void testAnalyseMultiSolution_WithImageBytes_ReturnsAnalysisResult() {
    // Run twice because of internal async usage

    var future1 = anthropicService.analyseMultiSolution(testImage);
    var result1 = future1.join();
    assertTrue(result1.isComplete());

    var future2 = anthropicService.analyseMultiSolution(testImage);
    var result2 = future2.join();
    assertTrue(result2.isComplete());
  }

  @Test
  void testAnalyseMultiSolutionMock_Message_ReturnsAnalysisResult() throws IOException {
    var mockResponse = fileService.loadResourceFile(ViewController.MULTI_SOLUTION_MOCK_RESPONSE_FILE_PATH);
    var future = anthropicService.analyseMultiSolutionMock(mockResponse);
    var multiSolutionResult = future.join();

    // Test that we have at least one solution
    assertTrue(multiSolutionResult.hasAnySolutions());
    assertTrue(multiSolutionResult.getSolutionCount() >= 1);
    
    // Test the first solution contains expected content
    var firstSolution = multiSolutionResult.getSolution(0).get();
    assertTrue(
        firstSolution.getSolutionDescription().get().startsWith("HASH SET validation"),
        firstSolution.getSolutionDescription().get().substring(0, 20));
    assertTrue(
        firstSolution.getEdgeCases().get().startsWith("• Empty cells"),
        firstSolution.getEdgeCases().get().substring(0, 20));
    assertTrue(
        firstSolution.getSolutionCode().get().startsWith("class Solution {"),
        firstSolution.getSolutionCode().get().substring(0, 20));
    assertTrue(
        firstSolution.getTimeComplexity().get().startsWith("O(1) - Fixed 9x9"),
        firstSolution.getTimeComplexity().get().substring(0, 20));
    assertTrue(
        firstSolution.getSpaceComplexity().get().startsWith("O(1) - Fixed storage"),
        firstSolution.getSpaceComplexity().get().substring(0, 20));
    assertTrue(
        multiSolutionResult.getSharedProblemStatement().get().contains("36. Valid Sudoku"),
        multiSolutionResult.getSharedProblemStatement().get().substring(0, 20));
  }
}
