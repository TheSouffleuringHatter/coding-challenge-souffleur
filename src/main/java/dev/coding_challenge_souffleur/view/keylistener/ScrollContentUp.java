package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class ScrollContentUp implements KeyHandler {

  private final MultiSolutionTabPane multiSolutionTabPane;
  private final Win32VK scrollUpKeyCode;

  @Inject
  ScrollContentUp(
      @ConfigProperty(name = "app.keyboard.key.scroll_up") final Win32VK scrollUpKeyCode,
      final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.scrollUpKeyCode = scrollUpKeyCode;
  }

  @Override
  public void performAction() {
    if (multiSolutionTabPane != null && multiSolutionTabPane.isVisible()) {
      var selectedTab = multiSolutionTabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof final ScrollPane scrollPane) {
        var vvalue = scrollPane.getVvalue();
        var newValue = Math.clamp(vvalue - ScrollContentDown.SCROLL_INCREMENT, 0.0, 1.0);
        scrollPane.setVvalue(newValue);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return scrollUpKeyCode;
  }
}
