package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ScreenshotDisplayService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TakeScreenshotTest {

  @Mock private ScreenshotDisplayService screenshotDisplayService;

  @Test
  void performAction_ShouldCallTakeScreenshotAndShowPreview() {
    new TakeScreenshot(Win32VK.VK_R, screenshotDisplayService).performAction();

    verify(screenshotDisplayService, times(1)).takeScreenshotOfForegroundWindowAndShowPreview();
  }
}
