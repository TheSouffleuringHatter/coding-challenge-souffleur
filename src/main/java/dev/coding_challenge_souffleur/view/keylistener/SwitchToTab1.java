package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class SwitchToTab1 implements KeyHandler {

  static final Win32VK SWITCH_TO_TAB1_KEY_CODE = Win32VK.VK_1;

  private final MultiSolutionTabPane multiSolutionTabPane;

  @Inject
  SwitchToTab1(final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
  }

  @Override
  public void performAction() {
    if (multiSolutionTabPane != null && multiSolutionTabPane.isVisible()) {
      var tabs = multiSolutionTabPane.getTabs();
      if (!tabs.isEmpty()) {
        multiSolutionTabPane.getSelectionModel().select(0);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SWITCH_TO_TAB1_KEY_CODE;
  }
}
