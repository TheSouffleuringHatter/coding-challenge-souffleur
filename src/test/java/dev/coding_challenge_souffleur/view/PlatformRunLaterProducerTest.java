package dev.coding_challenge_souffleur.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlatformRunLaterProducerTest {

  @Test
  void shouldProducePlatformRunLaterInstance() {
    PlatformRunLaterProducer producer = new PlatformRunLaterProducer();

    PlatformRunLater platformRunLater = producer.platformRunLater();

    assertNotNull(platformRunLater);
  }

  @Test
  void shouldProduceFunctionalInterface() {
    PlatformRunLaterProducer producer = new PlatformRunLaterProducer();

    PlatformRunLater platformRunLater = producer.platformRunLater();

    assertNotNull(platformRunLater);
    assertTrue(platformRunLater instanceof PlatformRunLater);
  }

  @Test
  void shouldProduceConsistentInstance() {
    PlatformRunLaterProducer producer = new PlatformRunLaterProducer();

    PlatformRunLater first = producer.platformRunLater();
    PlatformRunLater second = producer.platformRunLater();

    assertNotNull(first);
    assertNotNull(second);
  }
}
