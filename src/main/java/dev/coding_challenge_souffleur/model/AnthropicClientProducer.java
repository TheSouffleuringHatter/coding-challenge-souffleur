package dev.coding_challenge_souffleur.model;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import dev.coding_challenge_souffleur.ConfigurationKeys;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class AnthropicClientProducer {

  private final String apiKey;

  @Produces private AnthropicClient anthropicClient;

  @Inject
  AnthropicClientProducer(
      @ConfigProperty(name = ConfigurationKeys.ANTHROPIC_API_KEY) final String apiKey) {
    this.apiKey = apiKey;
  }

  @PostConstruct
  private void init() {
    anthropicClient = AnthropicOkHttpClient.builder().apiKey(apiKey).build();
  }
}
