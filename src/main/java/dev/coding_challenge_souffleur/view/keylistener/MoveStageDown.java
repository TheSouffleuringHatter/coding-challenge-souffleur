package dev.coding_challenge_souffleur.view.keylistener;

import static dev.coding_challenge_souffleur.view.keylistener.MoveStageUp.MOVE_AMOUNT;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class MoveStageDown implements KeyHandler {

  private final Stage stage;
  private final Win32VK moveDownKeyCode;

  @Inject
  MoveStageDown(
      @ConfigProperty(name = "app.keyboard.key.move_down") final Win32VK moveDownKeyCode,
      final Stage stage) {
    this.stage = stage;
    this.moveDownKeyCode = moveDownKeyCode;
  }

  @Override
  public void performAction() {
    stage.setY(stage.getY() + MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return moveDownKeyCode;
  }
}
