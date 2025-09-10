package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
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
class ScrollContentDownTest {

  @Mock private MultiSolutionTabPane multiSolutionTabPane;
  @Mock private SingleSelectionModel<Tab> selectionModel;

  @Test
  void performAction_ShouldScrollDownSelectedTab() {
    var scrollPane = new ScrollPane();
    scrollPane.setVvalue(0.6);

    var tab = new Tab();
    tab.setContent(scrollPane);

    when(multiSolutionTabPane.isVisible()).thenReturn(true);
    when(multiSolutionTabPane.getSelectionModel()).thenReturn(selectionModel);
    when(selectionModel.getSelectedItem()).thenReturn(tab);

    new ScrollContentDown(Win32VK.VK_DOWN, multiSolutionTabPane).performAction();

    assertEquals(0.8, scrollPane.getVvalue());
  }
}
