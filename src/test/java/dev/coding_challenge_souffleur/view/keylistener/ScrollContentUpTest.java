package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
@Isolated
class ScrollContentUpTest {

  @Test
  void performAction_ShouldCallScrollContentUp() {
    var contentPane = new ScrollPane();
    contentPane.setVvalue(0.4);

    new ScrollContentUp(contentPane).performAction();

    assertEquals(0.2, contentPane.getVvalue());
  }
}
