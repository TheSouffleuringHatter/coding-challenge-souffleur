package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScreenshotServiceTest {

  private ScreenshotService screenshotService;

  @BeforeEach
  void setUp() {
    var imageService = new ImageService();
    screenshotService = new ScreenshotService(false, imageService);
  }

  @Test
  void getScreenshot_WhenNoScreenshotTaken_ShouldReturnEmpty() {
    var result = screenshotService.getScreenshot();
    assertTrue(result.isEmpty());
  }
}
