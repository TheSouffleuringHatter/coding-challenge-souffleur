package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class RunAnalysis implements KeyHandler {

  private final ViewController viewController;
  private final Win32VK runAnalysisKeyCode;

  @Inject
  RunAnalysis(@ConfigProperty(name = "app.keyboard.key.run_analysis") final Win32VK runAnalysisKeyCode,
              final ViewController viewController) {
    this.viewController = viewController;
    this.runAnalysisKeyCode = runAnalysisKeyCode;
  }

  @Override
  public void performAction() {
    viewController.executeMultiSolutionAnalysis();
  }

  @Override
  public Win32VK getKeyCode() {
    return runAnalysisKeyCode;
  }
}
