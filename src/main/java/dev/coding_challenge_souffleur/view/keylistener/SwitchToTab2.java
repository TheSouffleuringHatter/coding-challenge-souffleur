package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class SwitchToTab2 implements KeyHandler {

  private final MultiSolutionTabPane multiSolutionTabPane;
  private final Win32VK switchToTab2KeyCode;

  @Inject
  SwitchToTab2(@ConfigProperty(name = "app.keyboard.key.switch_to_tab2") final Win32VK switchToTab2KeyCode,
               final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.switchToTab2KeyCode = switchToTab2KeyCode;
  }

  @Override
  public void performAction() {
    if (multiSolutionTabPane != null && multiSolutionTabPane.isVisible()) {
      var tabs = multiSolutionTabPane.getTabs();
      if (tabs.size() > 1) {
        multiSolutionTabPane.getSelectionModel().select(1);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return switchToTab2KeyCode;
  }
}
