package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RunMockAnalysisTest {

  @Mock private ViewController viewController;

  @Test
  void performAction_ShouldDelegateToViewController() {
    new RunMockAnalysis(Win32VK.VK_U, viewController).performAction();

    verify(viewController, times(1)).executeMultiSolutionMockAnalysis();
  }
}
