package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class SwitchToTab1 implements KeyHandler {

  static final Win32VK SWITCH_TO_TAB1_KEY_CODE = Win32VK.VK_1;

  private final ViewController viewController;

  @Inject
  SwitchToTab1(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    if (viewController.solutionTabPane != null && viewController.solutionTabPane.isVisible()) {
      var tabs = viewController.solutionTabPane.getTabs();
      if (!tabs.isEmpty()) {
        viewController.solutionTabPane.getSelectionModel().select(0);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SWITCH_TO_TAB1_KEY_CODE;
  }
}
