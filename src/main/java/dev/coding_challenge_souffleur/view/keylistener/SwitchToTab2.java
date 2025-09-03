package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class SwitchToTab2 implements KeyHandler {

  static final Win32VK SWITCH_TO_TAB2_KEY_CODE = Win32VK.VK_2;

  private final ViewController viewController;

  @Inject
  SwitchToTab2(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    if (viewController.solutionTabPane != null && viewController.solutionTabPane.isVisible()) {
      var tabs = viewController.solutionTabPane.getTabs();
      if (tabs.size() > 1) {
        viewController.solutionTabPane.getSelectionModel().select(1);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SWITCH_TO_TAB2_KEY_CODE;
  }
}
