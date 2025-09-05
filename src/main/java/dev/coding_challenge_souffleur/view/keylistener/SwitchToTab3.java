package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class SwitchToTab3 implements KeyHandler {

  static final Win32VK SWITCH_TO_TAB3_KEY_CODE = Win32VK.VK_3;

  private final MultiSolutionTabPane multiSolutionTabPane;

  @Inject
  SwitchToTab3(final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
  }

  @Override
  public void performAction() {
    if (multiSolutionTabPane != null && multiSolutionTabPane.isVisible()) {
      var tabs = multiSolutionTabPane.getTabs();
      if (tabs.size() > 2) {
        multiSolutionTabPane.getSelectionModel().select(2);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SWITCH_TO_TAB3_KEY_CODE;
  }
}
