package dev.coding_challenge_souffleur.model;

import com.anthropic.helpers.MessageAccumulator;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class MultiSolutionStreamProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiSolutionStreamProcessor.class);

  /**
   * Returns the content of the given section from the provided text. Prefers complete section
   * content; if not present, falls back to the latest partial content. Returns empty if not found.
   */
  private static Optional<String> extractSectionContent(
      final String text, final SolutionSection solutionSection) {
    if (text == null || text.isEmpty() || solutionSection == null) {
      return Optional.empty();
    }

    var completeMatcher = solutionSection.completePattern().matcher(text);
    if (completeMatcher.find()) {
      return Optional.of(completeMatcher.group(1).trim());
    }

    var partialMatcher = solutionSection.partialPattern().matcher(text);
    if (partialMatcher.find()) {
      var partial = partialMatcher.group(1).trim();
      if (!partial.isEmpty()) {
        return Optional.of(partial);
      }
    }

    return Optional.empty();
  }

  /**
   * Attempts to extract the content for the provided section from text and update the result.
   * Returns true if the result was updated (complete or partial content changed), otherwise false.
   */
  private static boolean extractAndUpdate(
      final StreamingAnalysisResult result,
      final SolutionSection solutionSection,
      final String text) {
    if (text == null || text.isEmpty() || solutionSection == null || result == null) {
      return false;
    }

    var contentOpt = extractSectionContent(text, solutionSection);
    if (contentOpt.isPresent()) {
      var newValue = contentOpt.get();
      var currentValue = result.getSection(solutionSection).orElse("");
      if (!newValue.equals(currentValue)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Updated {} from parsed content", solutionSection.name());
        }
        result.setSection(solutionSection, newValue);
        return true;
      }
    }

    return false;
  }

  private static void dumpTextContent(final String textContent) {
    if (textContent == null || textContent.trim().isEmpty()) {
      LOGGER.debug("No text content to dump");
      return;
    }

    try {
      var timestamp = System.currentTimeMillis();
      var fileName = "multi_solution_error_dump_" + timestamp + ".txt";
      var path = Path.of(System.getProperty("java.io.tmpdir"), fileName);
      Files.writeString(path, textContent);
      LOGGER.debug("Multi-solution response text dumped to {}", path);
    } catch (final IOException | SecurityException e) {
      LOGGER.debug("Failed to write response dump: {}", e.getMessage());
    } catch (final Exception e) {
      LOGGER.debug("Unexpected error during response dump: {}", e.getMessage(), e);
    }
  }

  private static String extractCompleteResponse(final MessageAccumulator messageAccumulator) {
    return messageAccumulator.message().content().stream()
        .filter(block -> block.isText() && block.text().isPresent())
        .map(block -> block.text().get().text())
        .collect(Collectors.joining());
  }

  private static void notifyCallback(
      final Consumer<MultiSolutionResult> callback, final MultiSolutionResult result) {
    if (callback != null) {
      callback.accept(result);
    }
  }

  private static String[] splitIntoSolutionBlocks(final String text) {
    // If text is empty or whitespace-only, return no solution blocks
    if (text == null || text.trim().isEmpty()) {
      return new String[0];
    }

    // If no solution titles found, check if it contains solution content
    var matcher = SolutionSection.SOLUTION_BOUNDARY_PATTERN_INSTANCE.matcher(text);
    if (!matcher.find()) {
      // If text contains solution-related sections, treat as single solution
      if (SolutionSection.containsSolutionContent(text)) {
        return new String[] {text};
      }
      // Otherwise (only problem statement, etc.), return no solution blocks
      return new String[0];
    }

    // Split by solution titles, keeping the SOLUTION_TITLE: part
    var parts = SolutionSection.SOLUTION_BOUNDARY_PATTERN_INSTANCE.split(text);
    if (parts.length <= 1) {
      return new String[] {text};
    }

    // Reconstruct solution blocks, adding back the SOLUTION_TITLE: prefix
    var result = new String[parts.length - 1]; // Skip first part (before first SOLUTION_TITLE)
    for (var i = 1; i < parts.length; i++) {
      result[i - 1] = SolutionSection.SOLUTION_TITLE.headerPrefix() + parts[i];
    }

    return result;
  }

  private boolean updateSharedProblemStatement(
      final MultiSolutionResult result, final String text) {
    return extractSectionContent(text, SolutionSection.PROBLEM_STATEMENT)
        .map(String::trim)
        .filter(ps -> !ps.equals(result.getSharedProblemStatement().orElse("")))
        .map(
            ps -> {
              result.setSharedProblemStatement(ps);
              return true;
            })
        .orElse(false);
  }

  private boolean updateSingleSolution(
      final StreamingAnalysisResult solution, final String solutionText) {
    var updated = false;

    for (final var section : SolutionSection.values()) {
      // Skip PROBLEM_STATEMENT for individual solutions since it's shared
      if (section == SolutionSection.PROBLEM_STATEMENT) {
        continue;
      }

      if (extractAndUpdate(solution, section, solutionText)) {
        updated = true;
      }
    }

    return updated;
  }

  void processStreamEvents(
      final MultiSolutionResult result,
      final StringBuilder accumulatedText,
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
        dumpTextContent(completeResponse);
      }
    } catch (final Exception e) {
      LOGGER.warn("Error handling final response", e);
      notifyCallback(updateCallback, result);
    }
  }

  private boolean updateMultiSolutionResult(final String text, final MultiSolutionResult result) {
    var updated = updateSharedProblemStatement(result, text);

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
        if (updateSingleSolution(solutionResult, solutionBlock)) {
          updated = true;
        }
      }
    }

    return updated;
  }
}
