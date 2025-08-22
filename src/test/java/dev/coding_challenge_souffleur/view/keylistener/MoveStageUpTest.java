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
@AddBeanClasses(MoveStageUp.class)
@ExtendWith(MockitoExtension.class)
class MoveStageUpTest {

  @Produces @Mock private Stage stage;

  @Inject private MoveStageUp moveStageUp;

  @Test
  void performAction_ShouldDecreaseStageYCoordinateByMoveAmount() {
    // Arrange
    var initialY = 100.0;
    when(stage.getY()).thenReturn(initialY);

    // Act
    moveStageUp.performAction();

    // Assert
    verify(stage).getY();
    verify(stage).setY(initialY - MoveStageUp.MOVE_AMOUNT);
  }
}
