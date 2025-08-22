package dev.coding_challenge_souffleur.view.keylistener;

import static dev.coding_challenge_souffleur.view.keylistener.MoveStageUp.MOVE_AMOUNT;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
class MoveStageDown implements KeyHandler {

  static final Win32VK MOVE_DOWN_KEY_CODE = Win32VK.VK_D;

  private final Stage stage;

  @Inject
  MoveStageDown(final Stage stage) {
    this.stage = stage;
  }

  @Override
  public void performAction() {
    stage.setY(stage.getY() + MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return MOVE_DOWN_KEY_CODE;
  }
}
