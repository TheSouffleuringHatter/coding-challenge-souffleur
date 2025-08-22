package dev.coding_challenge_souffleur;

import javafx.application.Application;

/**
 * Dedicated launcher class for the JavaFX application.
 *
 * <p>This class works around a known IntelliJ IDEA bug (IDEA-232340) where JavaFX applications fail
 * to start with "JavaFX runtime components are missing" error when run directly from the IDE.
 *
 * <p>The workaround uses a separate launcher class that explicitly invokes Application.launch()
 * with the main application class as a parameter.
 *
 * @see <a href="https://youtrack.jetbrains.com/issue/IDEA-232340">IDEA-232340 issue tracker</a>
 * @see <a href="https://stackoverflow.com/a/59942232/1645517">Stack Overflow solution details</a>
 */
public final class ApplicationLauncher {

  public static void main(final String... args) {
    Application.launch(JavaFxApplication.class, args);
  }
}
