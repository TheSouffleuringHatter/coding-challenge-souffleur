package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.scene.control.ScrollPane;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class ScrollContentDown implements KeyHandler {

  static final double SCROLL_INCREMENT = 0.2;

  private final MultiSolutionTabPane multiSolutionTabPane;
  private final Win32VK scrollDownKeyCode;

  @Inject
  ScrollContentDown(@ConfigProperty(name = "app.keyboard.key.scroll_down") final Win32VK scrollDownKeyCode,
                    final MultiSolutionTabPane multiSolutionTabPane) {
    this.multiSolutionTabPane = multiSolutionTabPane;
    this.scrollDownKeyCode = scrollDownKeyCode;
  }

  @Override
  public void performAction() {
    if (multiSolutionTabPane != null && multiSolutionTabPane.isVisible()) {
      var selectedTab = multiSolutionTabPane.getSelectionModel().getSelectedItem();
      if (selectedTab != null && selectedTab.getContent() instanceof final ScrollPane scrollPane) {
        var vvalue = scrollPane.getVvalue();
        var newValue = Math.clamp(vvalue + SCROLL_INCREMENT, 0.0, 1.0);
        scrollPane.setVvalue(newValue);
      }
    }
  }

  @Override
  public Win32VK getKeyCode() {
    return scrollDownKeyCode;
  }
}
