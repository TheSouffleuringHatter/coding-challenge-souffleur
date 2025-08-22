package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import dev.coding_challenge_souffleur.view.PlatformRunLater;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableAutoWeld
@AddBeanClasses(Exit.class)
@ExtendWith(MockitoExtension.class)
class ExitTest {

  @Produces @Mock private ViewController viewController;
  @Produces @Mock private PlatformRunLater platformRunLater;

  @Inject private Exit exit;

  @Test
  void performAction_ShouldCallExitOnViewController() {
    exit.performAction();
    verify(viewController, times(1)).exit();
  }
}
