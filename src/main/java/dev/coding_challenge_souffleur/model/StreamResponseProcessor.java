package dev.coding_challenge_souffleur.model;

import com.anthropic.helpers.MessageAccumulator;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
class StreamResponseProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(StreamResponseProcessor.class);

  void processStreamEvents(
      final StringBuilder accumulatedText,
      final StreamingAnalysisResult result,
      final Consumer<StreamingAnalysisResult> updateCallback,
      final String textDelta) {

    try {
      accumulatedText.append(textDelta);
      if (updateResult(accumulatedText.toString(), result)) {
        notifyCallback(updateCallback, result);
      }
    } catch (final Exception e) {
      // Continue processing despite individual event errors
      LOGGER.warn("Error processing stream event", e);
    }
  }

  void handleFinalResponse(
      final StreamingAnalysisResult result,
      final Consumer<StreamingAnalysisResult> updateCallback,
      final MessageAccumulator messageAccumulator) {

    try {
      var completeResponse = extractCompleteResponse(messageAccumulator);
      if (updateResult(completeResponse, result)) {
        notifyCallback(updateCallback, result);
      }

      if (!result.isComplete()) {
        LOGGER.debug("Analysis result is incomplete, dumping response for debugging");
        dumpTextContentOnError(completeResponse);
      }
    } catch (final Exception e) {
      LOGGER.error("Error handling final response", e);
      // Ensure callback is notified even on error
      notifyCallback(updateCallback, result);
    }
  }

  private void dumpTextContentOnError(final String textContent) {
    if (textContent == null || textContent.trim().isEmpty()) {
      LOGGER.debug("No text content to dump");
      return;
    }

    try {
      var timestamp = System.currentTimeMillis();
      var fileName = "error_response_dump_" + timestamp + ".txt";
      var path = java.nio.file.Path.of(System.getProperty("java.io.tmpdir"), fileName);
      java.nio.file.Files.writeString(path, textContent);
      LOGGER.debug("Response text dumped to {}", path);
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

  private boolean updateResult(final String text, final StreamingAnalysisResult result) {
    var updated = false;
    for (final var section : AnalysisResultSection.values()) {
      if (section.extractAndUpdate(text, result)) {
        updated = true;
      }
    }
    return updated;
  }

  private void notifyCallback(
      final Consumer<StreamingAnalysisResult> callback, final StreamingAnalysisResult result) {
    if (callback != null) {
      callback.accept(result);
    }
  }
}
