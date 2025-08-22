package dev.coding_challenge_souffleur.view.keylistener;

import static dev.coding_challenge_souffleur.view.keylistener.MoveStageUp.MOVE_AMOUNT;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@ApplicationScoped
class MoveStageRight implements KeyHandler {

  static final Win32VK MOVE_RIGHT_KEY_CODE = Win32VK.VK_F;

  private final Stage stage;

  @Inject
  MoveStageRight(final Stage stage) {
    this.stage = stage;
  }

  @Override
  public void performAction() {
    stage.setX(stage.getX() + MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return MOVE_RIGHT_KEY_CODE;
  }
}
