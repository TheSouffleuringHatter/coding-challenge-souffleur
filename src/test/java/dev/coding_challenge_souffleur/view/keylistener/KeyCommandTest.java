package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyCommandTest {

  @Mock private KeyCommandDependencies mockDependencies;
  @Mock private Runnable mockExitApplication;

  @Test
  void shouldExecuteExitCommand() {
    when(mockDependencies.exitApplication()).thenReturn(mockExitApplication);
    KeyCommand.EXIT.execute(mockDependencies);
    verify(mockExitApplication).run();
  }

  @Test
  void shouldHaveCorrectConfigProperty() {
    var exitCommand = KeyCommand.EXIT;
    assert exitCommand.getKeyConfigProperty().equals("app.keyboard.key.exit");
  }
}
