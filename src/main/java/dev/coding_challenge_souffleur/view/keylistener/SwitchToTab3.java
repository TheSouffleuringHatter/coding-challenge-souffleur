package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class SwitchToTab3 implements KeyHandler {

  private final MultiSolutionTabPane multiSolutionTabPane;
  private final Win32VK switchToTab3KeyCode;

  @Inject
  SwitchToTab3(
      @ConfigProperty(name = "app.keyboard.key.switch_to_tab3") final Win32VK switchToTab3KeyCode,
      final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.switchToTab3KeyCode = switchToTab3KeyCode;
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
    return switchToTab3KeyCode;
  }
}
