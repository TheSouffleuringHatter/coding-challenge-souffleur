package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;

@ApplicationScoped
class ScrollContentDown implements KeyHandler {

  static final Win32VK SCROLL_DOWN_KEY_CODE = Win32VK.VK_5;

  static final double SCROLL_INCREMENT = 0.2;

  private final ViewController viewController;

  @Inject
  ScrollContentDown(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    var activeTabPane = viewController.getActiveTabPane();
    if (activeTabPane != null && activeTabPane.isVisible()) {
      var selectedTab = activeTabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof final ScrollPane scrollPane) {
        var vvalue = scrollPane.getVvalue();
        var newValue = Math.clamp(vvalue + SCROLL_INCREMENT, 0.0, 1.0);
        scrollPane.setVvalue(newValue);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SCROLL_DOWN_KEY_CODE;
  }
}
