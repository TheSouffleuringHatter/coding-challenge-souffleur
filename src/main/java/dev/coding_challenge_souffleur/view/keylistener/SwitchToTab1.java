package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class SwitchToTab1 implements KeyHandler {

  private final MultiSolutionTabPane multiSolutionTabPane;
  private final Win32VK switchToTab1KeyCode;

  @Inject
  SwitchToTab1(@ConfigProperty(name = "app.keyboard.key.switch_to_tab1") final Win32VK switchToTab1KeyCode,
               final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.switchToTab1KeyCode = switchToTab1KeyCode;
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
    return switchToTab1KeyCode;
  }
}
