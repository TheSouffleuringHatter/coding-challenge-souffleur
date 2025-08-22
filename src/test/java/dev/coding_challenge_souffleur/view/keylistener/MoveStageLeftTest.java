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
@AddBeanClasses(MoveStageLeft.class)
@ExtendWith(MockitoExtension.class)
class MoveStageLeftTest {

  @Produces @Mock private Stage stage;

  @Inject private MoveStageLeft moveStageLeft;

  @Test
  void performAction_ShouldDecreaseStageXCoordinateByMoveAmount() {
    // Arrange
    var initialX = 100.0;
    when(stage.getX()).thenReturn(initialX);

    // Act
    moveStageLeft.performAction();

    // Assert
    verify(stage).getX();
    verify(stage).setX(initialX - MoveStageUp.MOVE_AMOUNT);
  }
}
