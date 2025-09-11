package dev.coding_challenge_souffleur.view;

import static org.mockito.Mockito.*;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HideShowStateTest {

  @Mock private WindowFromScreenCaptureHider mockHider;
  @Mock private Stage mockStage;

  @Test
  void shouldHideStageWhenApplicationIsVisible() {
    var hideShowState = new HideShowState(mockHider);
    hideShowState.toggleVisibility(mockStage);

    verify(mockStage).hide();
    verify(mockStage, never()).show();
    verify(mockHider, never()).excludeWindowsFromScreenCapture();
  }

  @Test
  void shouldShowStageAndExcludeWindowsWhenApplicationIsNotVisible() {
    var hideShowState = new HideShowState(mockHider);
    hideShowState.toggleVisibility(mockStage);
    hideShowState.toggleVisibility(mockStage);

    verify(mockStage, times(1)).show();
    verify(mockStage, times(1)).hide();
    verify(mockHider, times(1)).excludeWindowsFromScreenCapture();
  }
}
