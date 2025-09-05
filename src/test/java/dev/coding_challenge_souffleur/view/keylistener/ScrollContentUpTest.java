package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.ViewController;
import dev.coding_challenge_souffleur.view.components.MultiSolutionTabPane;
import javafx.scene.control.ScrollPane;
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
class ScrollContentUpTest {

  @Mock private ViewController viewController;
  @Mock private MultiSolutionTabPane multiSolutionTabPane;
  @Mock private SingleSelectionModel<Tab> selectionModel;

  @Test
  void performAction_ShouldScrollUpSelectedTab() {
    var scrollPane = new ScrollPane();
    scrollPane.setVvalue(0.4);

    var tab = new Tab();
    tab.setContent(scrollPane);

    when(viewController.getActiveTabPane()).thenReturn(multiSolutionTabPane);
    when(multiSolutionTabPane.isVisible()).thenReturn(true);
    when(multiSolutionTabPane.getSelectionModel()).thenReturn(selectionModel);
    when(selectionModel.getSelectedItem()).thenReturn(tab);

    new ScrollContentUp(viewController).performAction();

    assertEquals(0.2, scrollPane.getVvalue());
  }
}
