package dev.coding_challenge_souffleur.view.keylistener;

import static dev.coding_challenge_souffleur.view.keylistener.MoveStageUp.MOVE_AMOUNT;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class MoveStageLeft implements KeyHandler {

  private final Stage stage;
  private final Win32VK moveLeftKeyCode;

  @Inject
  MoveStageLeft(
      @ConfigProperty(name = "app.keyboard.key.move_left") final Win32VK moveLeftKeyCode,
      final Stage stage) {
    this.stage = stage;
    this.moveLeftKeyCode = moveLeftKeyCode;
  }

  @Override
  public void performAction() {
    stage.setX(stage.getX() - MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return moveLeftKeyCode;
  }
}
