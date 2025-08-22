package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
class MoveStageUp implements KeyHandler {

  static final int MOVE_AMOUNT = 30;

  static final Win32VK MOVE_UP_KEY_CODE = Win32VK.VK_E;

  private final Stage stage;

  @Inject
  MoveStageUp(final Stage stage) {
    this.stage = stage;
  }

  @Override
  public void performAction() {
    stage.setY(stage.getY() - MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return MOVE_UP_KEY_CODE;
  }
}
