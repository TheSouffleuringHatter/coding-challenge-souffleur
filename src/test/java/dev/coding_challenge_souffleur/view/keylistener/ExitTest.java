package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExitTest {

  @Mock private Runnable exitApplication;

  @Test
  void performAction_ShouldCallExitApplication() {
    var exit = new Exit(Win32VK.VK_Q, exitApplication);

    exit.performAction();

    verify(exitApplication, times(1)).run();
  }
}
