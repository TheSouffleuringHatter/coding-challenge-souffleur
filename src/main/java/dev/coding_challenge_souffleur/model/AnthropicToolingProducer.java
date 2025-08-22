package dev.coding_challenge_souffleur.model;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class AnthropicToolingProducer {

  private final String apiKey;

  @Produces private AnthropicClient anthropicClient;

  @Inject
  AnthropicToolingProducer(@ConfigProperty(name = "anthropic.api.key") final String apiKey) {
    this.apiKey = apiKey;
  }

  @PostConstruct
  private void init() {
    anthropicClient = AnthropicOkHttpClient.builder().apiKey(apiKey).build();
  }
}
