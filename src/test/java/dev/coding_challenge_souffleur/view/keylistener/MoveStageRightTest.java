package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoveStageRightTest {

  @Mock private Stage stage;

  @Test
  void performAction_ShouldIncreaseStageXCoordinateByMoveAmount() {
    // Arrange
    var initialX = 100.0;
    when(stage.getX()).thenReturn(initialX);
    var moveStageRight = new MoveStageRight(Win32VK.VK_F, stage);

    // Act
    moveStageRight.performAction();

    // Assert
    verify(stage).getX();
    verify(stage).setX(initialX + MoveStageUp.MOVE_AMOUNT);
  }
}
