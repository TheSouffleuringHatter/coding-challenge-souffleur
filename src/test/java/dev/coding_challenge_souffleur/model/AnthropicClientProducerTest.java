package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import com.anthropic.client.AnthropicClient;
import org.junit.jupiter.api.Test;

class AnthropicClientProducerTest {

  @Test
  void shouldCreateProducerWithApiKey() {
    String testApiKey = "test-api-key";

    AnthropicClientProducer producer = new AnthropicClientProducer(testApiKey);

    assertNotNull(producer);
  }

  @Test
  void shouldInitializeAnthropicClient() throws Exception {
    String testApiKey = "test-api-key";
    AnthropicClientProducer producer = new AnthropicClientProducer(testApiKey);

    // Use reflection to call the private init method
    var initMethod = AnthropicClientProducer.class.getDeclaredMethod("init");
    initMethod.setAccessible(true);
    initMethod.invoke(producer);

    // Use reflection to access the private anthropicClient field
    var clientField = AnthropicClientProducer.class.getDeclaredField("anthropicClient");
    clientField.setAccessible(true);
    AnthropicClient client = (AnthropicClient) clientField.get(producer);

    assertNotNull(client);
  }

  @Test
  void shouldCreateProducerEvenWithNullApiKey() {
    AnthropicClientProducer producer = new AnthropicClientProducer(null);

    assertNotNull(producer);
  }
}
