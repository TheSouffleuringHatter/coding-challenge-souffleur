package dev.coding_challenge_souffleur.view.keylistener;

import static org.mockito.Mockito.*;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.WindowFromScreenCaptureHider;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HideShowTest {

  @Mock private WindowFromScreenCaptureHider windowFromScreenCaptureHider;
  @Mock private Stage stage;

  private HideShow hideShow;

  @BeforeEach
  void setUp() {
    hideShow = new HideShow(Win32VK.VK_W, windowFromScreenCaptureHider, stage);
  }

  @Test
  void performAction_WhenApplicationIsVisible_ShouldHideStage() {
    // Act
    hideShow.performAction();

    // Assert
    verify(stage, times(1)).hide();
    verify(windowFromScreenCaptureHider, never()).excludeWindowsFromScreenCapture();
  }

  @Test
  void
      performAction_WhenApplicationIsNotVisible_ShouldShowStageAndExcludeWindowsFromScreenCapture() {
    // First call to make applicationIsVisible false
    hideShow.performAction();
    // Reset mocks to clear the first call
    reset(stage, windowFromScreenCaptureHider);

    // Act
    hideShow.performAction();

    // Assert
    verify(stage, times(1)).show();
    verify(windowFromScreenCaptureHider, times(1)).excludeWindowsFromScreenCapture();
  }
}
