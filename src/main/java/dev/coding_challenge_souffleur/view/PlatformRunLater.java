package dev.coding_challenge_souffleur.view;

import java.util.function.Consumer;

/**
 * Interface for executing code on the JavaFX application thread.
 *
 * <p>This provides a more explicit type than using Consumer<Runnable> directly, making the code
 * more readable and maintainable.
 */
@FunctionalInterface
public interface PlatformRunLater extends Consumer<Runnable> {

  /**
   * Executes the given Runnable on the JavaFX application thread.
   *
   * @param runnable The Runnable to execute on the JavaFX application thread
   */
  @Override
  void accept(Runnable runnable);
}
