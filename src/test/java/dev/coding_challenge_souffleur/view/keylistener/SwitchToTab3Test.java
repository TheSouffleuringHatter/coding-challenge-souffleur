package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.ViewController;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
@Isolated
class SwitchToTab3Test {

  @Mock private ViewController viewController;
  @Mock private TabPane solutionTabPane;
  @Mock private SingleSelectionModel<Tab> selectionModel;

  @Test
  void performAction_ShouldSelectThirdTab_WhenTabsArePresentAndVisible() {
    var tabs = FXCollections.observableArrayList(new Tab(), new Tab(), new Tab());

    viewController.solutionTabPane = solutionTabPane;
    when(solutionTabPane.isVisible()).thenReturn(true);
    when(solutionTabPane.getTabs()).thenReturn(tabs);
    when(solutionTabPane.getSelectionModel()).thenReturn(selectionModel);

    new SwitchToTab3(viewController).performAction();

    verify(selectionModel, times(1)).select(2);
  }
}
