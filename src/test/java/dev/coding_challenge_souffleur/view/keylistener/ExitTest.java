package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.ApplicationExitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExitTest {

  @Mock private ApplicationExitService applicationExitService;

  @Test
  void performAction_ShouldCallExitOnApplicationExitService() {
    new Exit(applicationExitService).performAction();

    verify(applicationExitService, times(1)).exitApplication();
  }
}
