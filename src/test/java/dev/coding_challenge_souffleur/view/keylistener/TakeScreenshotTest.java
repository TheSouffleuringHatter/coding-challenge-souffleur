package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

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
    new TakeScreenshot(screenshotDisplayService).performAction();

    verify(screenshotDisplayService, times(1)).takeScreenshotOfForegroundWindowAndShowPreview();
  }
}
