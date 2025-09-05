package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;

@ApplicationScoped
class ScrollContentUp implements KeyHandler {

  static final Win32VK SCROLL_UP_KEY_CODE = Win32VK.VK_4;

  private final MultiSolutionTabPane multiSolutionTabPane;

  @Inject
  ScrollContentUp(final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
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
    return SCROLL_UP_KEY_CODE;
  }
}
