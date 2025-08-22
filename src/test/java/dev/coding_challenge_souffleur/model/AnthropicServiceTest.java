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
  void testAnalyseStreaming_WithImageBytes_ReturnsAnalysisResult() {
    // Run twice because of internal aync usage

    var future1 = anthropicService.analyseStreaming(testImage);
    var result1 = future1.join();
    assertTrue(result1.isComplete());

    var future2 = anthropicService.analyseStreaming(testImage);
    var result2 = future2.join();
    assertTrue(result2.isComplete());
  }

  @Test
  void testAnalyseStreamingMock_Message_ReturnsAnalysisResult() throws IOException {
    var mockResponse = fileService.loadResourceFile(ViewController.MOCK_RESPONSE_FILE_PATH);
    var future = anthropicService.analyseStreamingMock(mockResponse);
    var streamingResult = future.join();

    assertTrue(
        streamingResult.getSolutionDescription().get().startsWith("HASH SET validation"),
        streamingResult.getSolutionDescription().get().substring(0, 20));
    assertTrue(
        streamingResult.getEdgeCases().get().startsWith("• Empty cells"),
        streamingResult.getEdgeCases().get().substring(0, 20));
    assertTrue(
        streamingResult.getSolutionCode().get().startsWith("class Solution {"),
        streamingResult.getSolutionCode().get().substring(0, 20));
    assertTrue(
        streamingResult.getTimeComplexity().get().startsWith("O(1) - Fixed 9x9"),
        streamingResult.getTimeComplexity().get().substring(0, 20));
    assertTrue(
        streamingResult.getSpaceComplexity().get().startsWith("O(1) - Fixed storage"),
        streamingResult.getSpaceComplexity().get().substring(0, 20));
    assertTrue(
        streamingResult.getProblemStatement().get().contains("36. Valid Sudoku"),
        streamingResult.getProblemStatement().get().substring(0, 20));
  }
}
