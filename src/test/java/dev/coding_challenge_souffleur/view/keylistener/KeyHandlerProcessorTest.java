package dev.coding_challenge_souffleur.view.keylistener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import java.util.List;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeyHandlerProcessorTest {

  @Mock private PlatformRunLater mockPlatformRunLater;
  @Mock private KeyCommandDependencies mockDependencies;
  @Mock private Config mockConfig;

  private KeyHandlerProcessor processor;

  @BeforeEach
  void setUp() {
    when(mockConfig.getValue(anyString(), eq(Win32VK.class))).thenReturn(Win32VK.VK_A);
    processor =
        new KeyHandlerProcessor(
            mockPlatformRunLater, List.of(Win32VK.VK_CONTROL), mockDependencies, mockConfig);
  }

  @Test
  void shouldCreateProcessorWithDependencies() {
    assertNotNull(processor);
  }
}
