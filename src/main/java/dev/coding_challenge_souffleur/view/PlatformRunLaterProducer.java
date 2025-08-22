package dev.coding_challenge_souffleur.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import javafx.application.Platform;

@ApplicationScoped
class PlatformRunLaterProducer {

  /**
   * Produces a lambda that wraps Platform.runLater().
   *
   * @return A PlatformRunLater that executes code on the JavaFx application thread
   */
  @Produces
  PlatformRunLater platformRunLater() {
    return Platform::runLater;
  }
}
