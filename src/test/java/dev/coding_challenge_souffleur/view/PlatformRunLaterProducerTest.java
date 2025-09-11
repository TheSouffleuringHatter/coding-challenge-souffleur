package dev.coding_challenge_souffleur.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlatformRunLaterProducerTest {

  @Test
  void shouldProducePlatformRunLaterInstance() {
    var producer = new PlatformRunLaterProducer();

    var platformRunLater = producer.platformRunLater();

    assertNotNull(platformRunLater);
  }

  @Test
  void shouldProduceFunctionalInterface() {
    var producer = new PlatformRunLaterProducer();

    var platformRunLater = producer.platformRunLater();

    assertNotNull(platformRunLater);
    assertInstanceOf(PlatformRunLater.class, platformRunLater);
  }

  @Test
  void shouldProduceConsistentInstance() {
    var producer = new PlatformRunLaterProducer();

    var first = producer.platformRunLater();
    var second = producer.platformRunLater();

    assertNotNull(first);
    assertNotNull(second);
  }
}
