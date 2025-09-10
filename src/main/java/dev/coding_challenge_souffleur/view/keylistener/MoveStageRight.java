package dev.coding_challenge_souffleur.view.keylistener;

import static dev.coding_challenge_souffleur.view.keylistener.MoveStageUp.MOVE_AMOUNT;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class MoveStageRight implements KeyHandler {

  private final Stage stage;
  private final Win32VK moveRightKeyCode;

  @Inject
  MoveStageRight(
      @ConfigProperty(name = "app.keyboard.key.move_right") final Win32VK moveRightKeyCode,
      final Stage stage) {
    this.stage = stage;
    this.moveRightKeyCode = moveRightKeyCode;
  }

  @Override
  public void performAction() {
    stage.setX(stage.getX() + MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return moveRightKeyCode;
  }
}
