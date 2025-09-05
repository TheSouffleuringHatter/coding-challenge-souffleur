package dev.coding_challenge_souffleur.model;

import com.anthropic.client.AnthropicClient;
import com.anthropic.helpers.MessageAccumulator;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.Base64ImageSource.MediaType;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import jakarta.annotation.PostConstruct;
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
  private final MultiSolutionStreamProcessor multiSolutionProcessor;
  private final FileService fileService;
  private final Model claudeModel;

  private String systemMessage;
  private String userMessage;
  private String multiSolutionMockText;

  @Inject
  AnthropicService(
      final AnthropicClient anthropicClient,
      final ImageService imageService,
      final MultiSolutionStreamProcessor multiSolutionStreamProcessor,
      final FileService fileService,
      @ConfigProperty(name = "anthropic.model") final Model claudeModel) {
    this.anthropicClient = anthropicClient;
    this.imageService = imageService;
    this.multiSolutionProcessor = multiSolutionStreamProcessor;
    this.fileService = fileService;
    this.claudeModel = claudeModel;
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

  private static ContentBlockParam createImageBlock(final byte[] imageBytes) {
    var base64Image = Base64.getEncoder().encodeToString(imageBytes);
    var imageSource =
        Base64ImageSource.builder().data(base64Image).mediaType(MediaType.IMAGE_PNG).build();
    return ContentBlockParam.ofImage(ImageBlockParam.builder().source(imageSource).build());
  }

  @PostConstruct
  void loadPrompts() {
    try {
      this.systemMessage =
          fileService.loadResourceFile("/prompts/system_prompt.txt")
              + fileService.loadResourceFile("/prompts/text_response_prompt.txt")
              + fileService.loadResourceFile("/prompts/java_prompt.txt")
              + fileService.loadResourceFile("/prompts/assistant_message.txt");
      this.userMessage = fileService.loadResourceFile("/prompts/user_message.txt");
      this.multiSolutionMockText = fileService.loadResourceFile("/prompts/multi_solution_mock.txt");
    } catch (final IOException e) {
      throw new RuntimeException("Failed to load prompt files", e);
    }
  }

  public CompletableFuture<MultiSolutionResult> analyseMultiSolution(
      final Image image, final Consumer<MultiSolutionResult> updateCallback) {
    try {
      var imageBytes = imageService.convertToByteArray(image);
      return analyseMultiSolution(imageBytes, updateCallback);
    } catch (final IOException e) {
      LOGGER.warn("Failed to convert image to byte array", e);
      return CompletableFuture.failedFuture(e);
    }
  }

  public CompletableFuture<MultiSolutionResult> analyseMultiSolutionMock(
      final Consumer<MultiSolutionResult> updateCallback) {
    return CompletableFuture.supplyAsync(
        () -> {
          var result = new MultiSolutionResult();
          var accumulatedText = new StringBuilder();

          var lines = multiSolutionMockText.lines().toList();
          var delayPerLine = Math.max(1, 2000 / lines.size());

          for (final var line : lines) {
            multiSolutionProcessor.processStreamEvents(
                result, accumulatedText, updateCallback, line + "\n");

            try {
              TimeUnit.MILLISECONDS.sleep(delayPerLine);
            } catch (final InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
            }
          }

          // Process the complete text one final time to ensure completion
          LOGGER.debug(
              "Final processing of complete mock text: {} characters", accumulatedText.length());
          multiSolutionProcessor.processStreamEvents(result, accumulatedText, updateCallback, "");

          if (updateCallback != null) {
            updateCallback.accept(result);
          }

          return result;
        });
  }

  CompletableFuture<MultiSolutionResult> analyseMultiSolution(
      final byte[] imageBytes, final Consumer<MultiSolutionResult> updateCallback) {
    return retryAsync(
        () -> processMultiSolutionRequest(imageBytes, new MultiSolutionResult(), updateCallback),
        1);
  }

  private MultiSolutionResult processMultiSolutionRequest(
      final byte[] imageBytes,
      final MultiSolutionResult result,
      final Consumer<MultiSolutionResult> updateCallback) {

    var params = createMessageParams(imageBytes);
    var accumulatedText = new StringBuilder();
    var messageAccumulator = MessageAccumulator.create();

    LOGGER.trace("Calling Anthropic API for multi-solution analysis...");
    try (var streamResponse = anthropicClient.messages().createStreaming(params)) {
      streamResponse.stream()
          .forEach(
              event -> {
                messageAccumulator.accumulate(event);
                event
                    .contentBlockDelta()
                    .flatMap(delta -> delta.delta().text())
                    .ifPresent(
                        textDelta -> {
                          var text = textDelta.text();
                          LOGGER.trace("Received text delta for multi-solution: {}", text);
                          multiSolutionProcessor.processStreamEvents(
                              result, accumulatedText, updateCallback, text);
                        });
              });

      multiSolutionProcessor.handleFinalResponse(result, updateCallback, messageAccumulator);
    } catch (final Exception e) {
      LOGGER.warn("Error in multi-solution streaming analysis", e);
      throw new RuntimeException(e);
    }

    return result;
  }

  private MessageCreateParams createMessageParams(final byte[] imageBytes) {
    return MessageCreateParams.builder()
        .maxTokens(10000)
        .system(systemMessage)
        .addUserMessageOfBlockParams(List.of(createImageBlock(imageBytes)))
        .addUserMessage(userMessage)
        .enabledThinking(5000)
        .model(claudeModel)
        .build();
  }
}
