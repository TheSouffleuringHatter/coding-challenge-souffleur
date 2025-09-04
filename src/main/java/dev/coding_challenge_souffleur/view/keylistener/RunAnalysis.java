package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class RunAnalysis implements KeyHandler {

  static final Win32VK ANALYSE_KEY_CODE = Win32VK.VK_T;

  private final ViewController viewController;

  @Inject
  RunAnalysis(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    viewController.executeMultiSolutionAnalysis();
  }

  @Override
  public Win32VK getKeyCode() {
    return ANALYSE_KEY_CODE;
  }
}
