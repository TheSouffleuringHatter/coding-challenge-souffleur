package windowskeyboardhook;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyboardHookFacadeTest {

  @Mock private KeyboardHookManager mockHookManager;
  @Mock private WindowsMessageLoopService mockMessageLoopService;
  @Mock private KeyboardEventProcessor mockEventProcessor;

  @Test
  void shouldCreateKeyboardHookFacade() {
    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    assertNotNull(facade);
  }

  @Test
  void shouldReturnRunningStatusWhenBothServicesActive() {
    when(mockHookManager.isHookInstalled()).thenReturn(true);
    when(mockMessageLoopService.isRunning()).thenReturn(true);

    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    assertTrue(facade.isRunning());
  }

  @Test
  void shouldReturnNotRunningWhenHookManagerNotInstalled() {
    when(mockHookManager.isHookInstalled()).thenReturn(false);

    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    assertFalse(facade.isRunning());
  }

  @Test
  void shouldReturnNotRunningWhenMessageLoopServiceNotRunning() {
    when(mockHookManager.isHookInstalled()).thenReturn(true);
    when(mockMessageLoopService.isRunning()).thenReturn(false);

    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    assertFalse(facade.isRunning());
  }

  @Test
  void shouldReturnNotRunningWhenBothServicesInactive() {
    when(mockHookManager.isHookInstalled()).thenReturn(false);

    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    assertFalse(facade.isRunning());
  }

  @Test
  void shouldCallCleanupServicesOnDestroy() {
    when(mockMessageLoopService.stopMessageLoop(anyInt())).thenReturn(true);
    when(mockHookManager.uninstallKeyboardHook()).thenReturn(true);
    when(mockMessageLoopService.shutdownExecutor(anyInt())).thenReturn(true);

    KeyboardHookFacade facade =
        new KeyboardHookFacade(mockHookManager, mockMessageLoopService, mockEventProcessor);

    facade.cleanup();

    verify(mockMessageLoopService).stopMessageLoop(3);
    verify(mockHookManager).uninstallKeyboardHook();
    verify(mockMessageLoopService).shutdownExecutor(2);
  }
}
