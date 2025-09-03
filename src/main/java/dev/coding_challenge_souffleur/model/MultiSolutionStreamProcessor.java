package dev.coding_challenge_souffleur.model;

import com.anthropic.helpers.MessageAccumulator;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class MultiSolutionStreamProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiSolutionStreamProcessor.class);
  private static final Pattern SOLUTION_BOUNDARY_PATTERN =
      Pattern.compile("SOLUTION_TITLE:", Pattern.DOTALL);

  void processStreamEvents(
      final StringBuilder accumulatedText,
      final MultiSolutionResult result,
      final Consumer<MultiSolutionResult> updateCallback,
      final String textDelta) {

    try {
      accumulatedText.append(textDelta);
      if (updateMultiSolutionResult(accumulatedText.toString(), result)) {
        notifyCallback(updateCallback, result);
      }
    } catch (final Exception e) {
      LOGGER.warn("Error processing stream event", e);
    }
  }

  void handleFinalResponse(
      final MultiSolutionResult result,
      final Consumer<MultiSolutionResult> updateCallback,
      final MessageAccumulator messageAccumulator) {

    try {
      var completeResponse = extractCompleteResponse(messageAccumulator);
      if (updateMultiSolutionResult(completeResponse, result)) {
        notifyCallback(updateCallback, result);
      }

      if (!result.isComplete()) {
        LOGGER.debug("Multi-solution result is incomplete, dumping response for debugging");
        dumpTextContentOnError(completeResponse);
      }
    } catch (final Exception e) {
      LOGGER.error("Error handling final response", e);
      notifyCallback(updateCallback, result);
    }
  }

  private boolean updateMultiSolutionResult(final String text, final MultiSolutionResult result) {
    var updated = updateSharedProblemStatement(text, result);

    // Split text into solution blocks
    var solutionBlocks = splitIntoSolutionBlocks(text);

    // Ensure we have enough StreamingAnalysisResult objects
    while (result.getSolutionCount() < solutionBlocks.length) {
      result.addSolution(new StreamingAnalysisResult());
    }

    // Update each solution
    for (int i = 0; i < solutionBlocks.length; i++) {
      var solutionBlock = solutionBlocks[i];
      if (result.getSolution(i).isPresent()) {
        var solutionResult = result.getSolution(i).get();
        if (updateSingleSolution(solutionBlock, solutionResult)) {
          updated = true;
        }
      }
    }

    return updated;
  }

  private String[] splitIntoSolutionBlocks(final String text) {
    // If text is empty or whitespace-only, return no solution blocks
    if (text == null || text.trim().isEmpty()) {
      return new String[0];
    }

    // If no solution titles found, check if it contains solution content
    var matcher = SOLUTION_BOUNDARY_PATTERN.matcher(text);
    if (!matcher.find()) {
      // If text contains solution-related sections, treat as single solution
      if (SolutionSection.containsSolutionContent(text)) {
        return new String[] {text};
      }
      // Otherwise (only problem statement, etc.), return no solution blocks
      return new String[0];
    }

    // Split by solution titles, keeping the SOLUTION_TITLE: part
    var parts = SOLUTION_BOUNDARY_PATTERN.split(text);
    if (parts.length <= 1) {
      return new String[] {text};
    }

    // Reconstruct solution blocks, adding back the SOLUTION_TITLE: prefix
    var result = new String[parts.length - 1]; // Skip first part (before first SOLUTION_TITLE)
    for (int i = 1; i < parts.length; i++) {
      result[i - 1] = "SOLUTION_TITLE:" + parts[i];
    }

    return result;
  }


  private boolean updateSharedProblemStatement(
      final String text, final MultiSolutionResult result) {
    var problemPattern =
        Pattern.compile("PROBLEM_STATEMENT:(.*?)" + SolutionSection.SECTION_END, Pattern.DOTALL);
    var matcher = problemPattern.matcher(text);

    if (matcher.find()) {
      var problemStatement = matcher.group(1).trim();
      if (!problemStatement.equals(result.getSharedProblemStatement().orElse(""))) {
        result.setSharedProblemStatement(problemStatement);
        return true;
      }
    }

    return false;
  }

  private boolean updateSingleSolution(
      final String solutionText, final StreamingAnalysisResult solution) {
    var updated = false;

    for (final var section : SolutionSection.values()) {
      // Skip PROBLEM_STATEMENT for individual solutions since it's shared
      if (section == SolutionSection.PROBLEM_STATEMENT) {
        continue;
      }

      if (SolutionSectionParser.extractAndUpdate(solutionText, section, solution)) {
        updated = true;
      }
    }

    return updated;
  }

  private void dumpTextContentOnError(final String textContent) {
    if (textContent == null || textContent.trim().isEmpty()) {
      LOGGER.debug("No text content to dump");
      return;
    }

    try {
      var timestamp = System.currentTimeMillis();
      var fileName = "multi_solution_error_dump_" + timestamp + ".txt";
      var path = java.nio.file.Path.of(System.getProperty("java.io.tmpdir"), fileName);
      java.nio.file.Files.writeString(path, textContent);
      LOGGER.debug("Multi-solution response text dumped to {}", path);
    } catch (final IOException | SecurityException e) {
      LOGGER.error("Failed to write response dump: {}", e.getMessage());
    } catch (final Exception e) {
      LOGGER.warn("Unexpected error during response dump: {}", e.getMessage(), e);
    }
  }

  private String extractCompleteResponse(final MessageAccumulator messageAccumulator) {
    return messageAccumulator.message().content().stream()
        .filter(block -> block.isText() && block.text().isPresent())
        .map(block -> block.text().get().text())
        .collect(Collectors.joining());
  }

  private void notifyCallback(
      final Consumer<MultiSolutionResult> callback, final MultiSolutionResult result) {
    if (callback != null) {
      callback.accept(result);
    }
  }
}
