package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class ToggleProblemStatement implements KeyHandler {

  private final ViewController viewController;
  private final Win32VK toggleProblemStatementKeyCode;

  @Inject
  ToggleProblemStatement(
      @ConfigProperty(name = "app.keyboard.key.toggle_problem_statement")
          final Win32VK toggleProblemStatementKeyCode,
      final ViewController viewController) {
    this.viewController = viewController;
    this.toggleProblemStatementKeyCode = toggleProblemStatementKeyCode;
  }

  @Override
  public void performAction() {
    viewController.toggleProblemStatement();
  }

  @Override
  public Win32VK getKeyCode() {
    return toggleProblemStatementKeyCode;
  }
}
