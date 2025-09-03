package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class ToggleProblemStatement implements KeyHandler {

  static final Win32VK TOGGLE_PROBLEM_KEY_CODE = Win32VK.VK_V;

  private final ViewController viewController;

  @Inject
  ToggleProblemStatement(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    viewController.toggleProblemStatement();
  }

  @Override
  public Win32VK getKeyCode() {
    return TOGGLE_PROBLEM_KEY_CODE;
  }
}