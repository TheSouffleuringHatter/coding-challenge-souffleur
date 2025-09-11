package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import com.anthropic.client.AnthropicClient;
import org.junit.jupiter.api.Test;

class AnthropicClientProducerTest {

  @Test
  void shouldCreateProducerWithApiKey() {
    var testApiKey = "test-api-key";

    var producer = new AnthropicClientProducer(testApiKey);

    assertNotNull(producer);
  }

  @Test
  void shouldInitializeAnthropicClient() throws Exception {
    var testApiKey = "test-api-key";
    var producer = new AnthropicClientProducer(testApiKey);

    // Use reflection to call the private init method
    var initMethod = AnthropicClientProducer.class.getDeclaredMethod("init");
    initMethod.setAccessible(true);
    initMethod.invoke(producer);

    // Use reflection to access the private anthropicClient field
    var clientField = AnthropicClientProducer.class.getDeclaredField("anthropicClient");
    clientField.setAccessible(true);
    var client = (AnthropicClient) clientField.get(producer);

    assertNotNull(client);
  }

  @Test
  void shouldCreateProducerEvenWithNullApiKey() {
    var producer = new AnthropicClientProducer(null);

    assertNotNull(producer);
  }
}
