package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
@Isolated
class SwitchToTab2Test {

  @Mock private MultiSolutionTabPane multiSolutionTabPane;
  @Mock private SingleSelectionModel<Tab> selectionModel;

  @Test
  void performAction_ShouldSelectSecondTab_WhenTabsArePresentAndVisible() {
    var tabs = FXCollections.observableArrayList(new Tab(), new Tab(), new Tab());

    when(multiSolutionTabPane.isVisible()).thenReturn(true);
    when(multiSolutionTabPane.getTabs()).thenReturn(tabs);
    when(multiSolutionTabPane.getSelectionModel()).thenReturn(selectionModel);

    new SwitchToTab2(multiSolutionTabPane).performAction();

    verify(selectionModel, times(1)).select(1);
  }
}
