package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.control.ScrollPane;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
@Isolated
class ScrollContentDownTest {

  @Test
  void performAction_ShouldCallScrollContentDown() {
    var contentPane = new ScrollPane();
    contentPane.setVvalue(0.6);

    new ScrollContentDown(contentPane).performAction();

    assertEquals(0.8, contentPane.getVvalue());
  }
}
