package dev.coding_challenge_souffleur.model;

import com.anthropic.client.AnthropicClient;
import com.anthropic.helpers.MessageAccumulator;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.Base64ImageSource.MediaType;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.scene.image.Image;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AnthropicService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnthropicService.class);
  private static final long DEFAULT_RETRY_BASE_DELAY_MS = 200;
  private static final int DEFAULT_RETRY_MAX_ATTEMPTS = 3;

  private final AnthropicClient anthropicClient;
  private final ImageService imageService;
  private final StreamResponseProcessor streamProcessor;
  private final PromptManager promptManager;
  private final Model claudeModel;

  @Inject
  AnthropicService(
      final AnthropicClient anthropicClient,
      final ImageService imageService,
      final StreamResponseProcessor streamResponseProcessor,
      final PromptManager promptManager,
      @ConfigProperty(name = "anthropic.model") final Model claudeModel) {
    this.anthropicClient = anthropicClient;
    this.imageService = imageService;
    this.streamProcessor = streamResponseProcessor;
    this.promptManager = promptManager;
    this.claudeModel = claudeModel;
  }

  private static void notifyCallback(
      final Consumer<StreamingAnalysisResult> callback, final StreamingAnalysisResult result) {
    if (callback != null) {
      callback.accept(result);
    }
  }

  private static <T> CompletableFuture<T> retryAsync(final Supplier<T> task, final int attempt) {
    if (attempt > 1) {
      LOGGER.debug("Retrying {}/{}", attempt, DEFAULT_RETRY_MAX_ATTEMPTS);
    }

    return CompletableFuture.supplyAsync(task)
        .handle(
            (res, ex) -> {
              if (ex == null) {
                return CompletableFuture.completedFuture(res);
              }

              var cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
              if (attempt >= DEFAULT_RETRY_MAX_ATTEMPTS) {
                return CompletableFuture.<T>failedFuture(cause);
              }

              var delay = DEFAULT_RETRY_BASE_DELAY_MS * attempt; // linear backoff
              return CompletableFuture.supplyAsync(
                      () -> null, CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS))
                  .thenCompose(v -> retryAsync(task, attempt + 1));
            })
        .thenCompose(Function.identity());
  }

  public CompletableFuture<StreamingAnalysisResult> analyseStreaming(
      final Image image, final Consumer<StreamingAnalysisResult> updateCallback) {
    try {
      var imageBytes = imageService.convertToByteArray(image);
      return analyseStreaming(imageBytes, updateCallback);
    } catch (final IOException e) {
      LOGGER.warn("Failed to convert image to byte array", e);
      return CompletableFuture.failedFuture(e);
    }
  }

  public CompletableFuture<StreamingAnalysisResult> analyseStreaming(final byte[] imageBytes) {
    return analyseStreaming(imageBytes, null);
  }

  public CompletableFuture<StreamingAnalysisResult> analyseStreaming(
      final byte[] imageBytes, final Consumer<StreamingAnalysisResult> updateCallback) {
    LOGGER.trace("Starting streaming analysis...");

    var result = new StreamingAnalysisResult();
    notifyCallback(updateCallback, result);

    Supplier<StreamingAnalysisResult> task =
        () -> processStreamingRequest(imageBytes, result, updateCallback);

    return retryAsync(task, 1);
  }

  public CompletableFuture<StreamingAnalysisResult> analyseStreamingMock(
      final String messageTextContent, final Consumer<StreamingAnalysisResult> updateCallback) {
    LOGGER.debug("Creating mock streaming result from text");

    var result = new StreamingAnalysisResult();
    notifyCallback(updateCallback, result);

    return CompletableFuture.supplyAsync(
        () -> {
          for (final var section : AnalysisResultSection.values()) {
            if (section.extractAndUpdate(messageTextContent, result) && updateCallback != null) {
              updateCallback.accept(result);
            }
          }
          LOGGER.debug("Mock streaming completed");
          return result;
        });
  }

  CompletableFuture<StreamingAnalysisResult> analyseStreamingMock(final String messageTextContent) {
    return analyseStreamingMock(messageTextContent, null);
  }

  private StreamingAnalysisResult processStreamingRequest(
      final byte[] imageBytes,
      final StreamingAnalysisResult result,
      final Consumer<StreamingAnalysisResult> updateCallback) {

    var params = createMessageParams(imageBytes);
    var accumulatedText = new StringBuilder();

    // Create fresh MessageAccumulator for each request to avoid state reuse
    var messageAccumulator = MessageAccumulator.create();

    // Anthropic API async documentation at
    // https://github.com/anthropics/anthropic-sdk-java#asynchronous-execution
    LOGGER.trace("Calling Anthropic API async...");
    try (var streamResponse = anthropicClient.messages().createStreaming(params)) {
      streamResponse.stream()
          .forEach(
              event -> {
                messageAccumulator.accumulate(event);
                event
                    .contentBlockDelta()
                    .flatMap(delta -> delta.delta().text())
                    .ifPresent(
                        textDelta ->
                            streamProcessor.processStreamEvents(
                                accumulatedText, result, updateCallback, textDelta.text()));
              });
    }

    streamProcessor.handleFinalResponse(result, updateCallback, messageAccumulator);
    LOGGER.debug(
        "Streaming completed, result status: {}", result.isComplete() ? "COMPLETE" : "INCOMPLETE");

    return result;
  }

  private MessageCreateParams createMessageParams(final byte[] imageBytes) {
    return MessageCreateParams.builder()
        .maxTokens(10000)
        .system(promptManager.getSystemMessage())
        .addUserMessageOfBlockParams(List.of(createImageBlock(imageBytes)))
        .addUserMessage(promptManager.getUserMessage())
        .enabledThinking(5000)
        .model(claudeModel)
        .build();
  }

  private ContentBlockParam createImageBlock(final byte[] imageBytes) {
    var base64Image = Base64.getEncoder().encodeToString(imageBytes);
    var imageSource =
        Base64ImageSource.builder().data(base64Image).mediaType(MediaType.IMAGE_PNG).build();
    return ContentBlockParam.ofImage(ImageBlockParam.builder().source(imageSource).build());
  }
}
