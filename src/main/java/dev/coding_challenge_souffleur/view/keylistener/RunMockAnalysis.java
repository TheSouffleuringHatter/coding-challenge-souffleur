package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class RunMockAnalysis implements KeyHandler {

  private final ViewController viewController;
  private final Win32VK runMockAnalysisKeyCode;

  @Inject
  RunMockAnalysis(
      @ConfigProperty(name = "app.keyboard.key.run_mock_analysis")
          final Win32VK runMockAnalysisKeyCode,
      final ViewController viewController) {
    this.viewController = viewController;
    this.runMockAnalysisKeyCode = runMockAnalysisKeyCode;
  }

  @Override
  public void performAction() {
    viewController.executeMultiSolutionMockAnalysis();
  }

  @Override
  public Win32VK getKeyCode() {
    return runMockAnalysisKeyCode;
  }
}
