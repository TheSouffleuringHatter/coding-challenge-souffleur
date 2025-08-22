package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import javafx.stage.Stage;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableAutoWeld
@AddBeanClasses(MoveStageRight.class)
@ExtendWith(MockitoExtension.class)
class MoveStageRightTest {

  @Produces @Mock private Stage stage;

  @Inject private MoveStageRight moveStageRight;

  @Test
  void performAction_ShouldIncreaseStageXCoordinateByMoveAmount() {
    // Arrange
    var initialX = 100.0;
    when(stage.getX()).thenReturn(initialX);

    // Act
    moveStageRight.performAction();

    // Assert
    verify(stage).getX();
    verify(stage).setX(initialX + MoveStageUp.MOVE_AMOUNT);
  }
}
