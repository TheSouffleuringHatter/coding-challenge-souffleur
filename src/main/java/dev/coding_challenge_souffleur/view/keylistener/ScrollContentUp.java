package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;

@ApplicationScoped
class ScrollContentUp implements KeyHandler {

  static final Win32VK SCROLL_UP_KEY_CODE = Win32VK.VK_3;

  private final ScrollPane contentPane;

  @Inject
  ScrollContentUp(final ScrollPane contentPane) {
    this.contentPane = contentPane;
  }

  @Override
  public void performAction() {
    if (contentPane != null && contentPane.isVisible()) {
      var vvalue = contentPane.getVvalue();
      var newValue = Math.clamp(vvalue - ScrollContentDown.SCROLL_INCREMENT, 0.0, 1.0);
      contentPane.setVvalue(newValue);
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SCROLL_UP_KEY_CODE;
  }
}
