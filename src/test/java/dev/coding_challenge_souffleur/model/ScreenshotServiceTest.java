
package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScreenshotServiceTest {

  private ScreenshotService screenshotService;
  private ImageService imageService;

  @BeforeEach
  void setUp() {
    imageService = new ImageService();
    screenshotService = new ScreenshotService(false, imageService);
  }

  @Test
  void getScreenshot_WhenNoScreenshotTaken_ShouldReturnEmpty() {
    var result = screenshotService.getScreenshot();
    assertTrue(result.isEmpty());
  }

  @Test
  void constructor_WithSaveToFileEnabled_ShouldNotThrow() {
    assertDoesNotThrow(() -> new ScreenshotService(true, imageService));
  }

  @Test
  void constructor_WithSaveToFileDisabled_ShouldNotThrow() {
    assertDoesNotThrow(() -> new ScreenshotService(false, imageService));
  }

  @Test
  void getScreenshot_ReturnsOptional() {
    var result = screenshotService.getScreenshot();
    assertNotNull(result);
  }

  @Test
  void getScreenshot_InitiallyReturnsEmptyOptional() {
    var result = screenshotService.getScreenshot();
    assertNotNull(result);
    assertFalse(result.isPresent());
  }
}
