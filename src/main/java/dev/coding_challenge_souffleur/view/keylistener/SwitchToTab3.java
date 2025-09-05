package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class SwitchToTab3 implements KeyHandler {

  static final Win32VK SWITCH_TO_TAB3_KEY_CODE = Win32VK.VK_3;

  private final ViewController viewController;

  @Inject
  SwitchToTab3(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    var activeTabPane = viewController.getActiveTabPane();
    if (activeTabPane != null && activeTabPane.isVisible()) {
      var tabs = activeTabPane.getTabs();
      if (tabs.size() > 2) {
        activeTabPane.getSelectionModel().select(2);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SWITCH_TO_TAB3_KEY_CODE;
  }
}
