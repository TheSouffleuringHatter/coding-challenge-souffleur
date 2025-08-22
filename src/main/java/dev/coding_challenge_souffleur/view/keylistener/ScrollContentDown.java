package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;

@ApplicationScoped
class ScrollContentDown implements KeyHandler {

  static final Win32VK SCROLL_DOWN_KEY_CODE = Win32VK.VK_C;

  static final double SCROLL_INCREMENT = 0.2;

  private final ScrollPane contentPane;

  @Inject
  ScrollContentDown(final ScrollPane contentPane) {
    this.contentPane = contentPane;
  }

  @Override
  public void performAction() {
    if (contentPane != null && contentPane.isVisible()) {
      var vvalue = contentPane.getVvalue();
      var newValue = Math.clamp(vvalue + SCROLL_INCREMENT, 0.0, 1.0);
      contentPane.setVvalue(newValue);
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return SCROLL_DOWN_KEY_CODE;
  }
}
