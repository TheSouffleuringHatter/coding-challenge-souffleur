package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MoveStageUpTest {

  @Mock private Stage stage;

  @Test
  void performAction_ShouldDecreaseStageYCoordinateByMoveAmount() {
    // Arrange
    var initialY = 100.0;
    when(stage.getY()).thenReturn(initialY);
    var moveStageUp = new MoveStageUp(Win32VK.VK_E, stage);

    // Act
    moveStageUp.performAction();

    // Assert
    verify(stage).getY();
    verify(stage).setY(initialY - MoveStageUp.MOVE_AMOUNT);
  }
}
