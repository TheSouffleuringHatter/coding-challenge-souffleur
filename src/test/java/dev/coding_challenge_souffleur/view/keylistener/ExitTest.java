package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExitTest {

  @Mock private Runnable exitApplication;

  @Test
  void performAction_ShouldCallExitApplication() {
    var exit = new Exit(exitApplication);

    exit.performAction();

    verify(exitApplication, times(1)).run();
  }
}
