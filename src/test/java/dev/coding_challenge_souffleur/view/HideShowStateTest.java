package dev.coding_challenge_souffleur.view;

import static org.mockito.Mockito.*;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HideShowStateTest {

  @Mock private WindowFromScreenCaptureHider mockHider;
  @Mock private Stage mockStage;
  @InjectMocks private HideShowState hideShowState;

  @Test
  void shouldHideStageWhenApplicationIsVisible() {
    hideShowState.toggleVisibility();

    verify(mockStage).hide();
    verify(mockStage, never()).show();
    verify(mockHider, never()).excludeWindowsFromScreenCapture();
  }

  @Test
  void shouldShowStageAndExcludeWindowsWhenApplicationIsNotVisible() {
    hideShowState.toggleVisibility();
    hideShowState.toggleVisibility();

    verify(mockStage, times(1)).show();
    verify(mockStage, times(1)).hide();
    verify(mockHider, times(1)).excludeWindowsFromScreenCapture();
  }
}
