package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class MoveStageUp implements KeyHandler {

  static final int MOVE_AMOUNT = 30;

  private final Stage stage;
  private final Win32VK moveUpKeyCode;

  @Inject
  MoveStageUp(@ConfigProperty(name = "app.keyboard.key.move_up") final Win32VK moveUpKeyCode,
              final Stage stage) {
    this.stage = stage;
    this.moveUpKeyCode = moveUpKeyCode;
  }

  @Override
  public void performAction() {
    stage.setY(stage.getY() - MOVE_AMOUNT);
  }

  @Override
  public Win32VK getKeyCode() {
    return moveUpKeyCode;
  }
}
