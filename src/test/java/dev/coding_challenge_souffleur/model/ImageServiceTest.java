package dev.coding_challenge_souffleur.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageServiceTest {

  private static final int TEST_IMAGE_WIDTH = 10;

  private static final int TEST_IMAGE_HEIGHT = 10;

  private ImageService imageService;

  private Image testImage;

  /** Creates a simple test image with a red pixel in the center. */
  private static Image createTestImage(final int width, final int height) {
    var image = new WritableImage(width, height);
    var pixelWriter = image.getPixelWriter();

    // Fill with white
    for (var y = 0; y < height; y++) {
      for (var x = 0; x < width; x++) {
        pixelWriter.setColor(x, y, Color.WHITE);
      }
    }

    // Add a red pixel in the center
    pixelWriter.setColor(width / 2, height / 2, Color.RED);

    return image;
  }

  @BeforeEach
  void setUp() {
    imageService = new ImageService();
    testImage = createTestImage(TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT);
  }

  @Test
  void convertToByteArray_ShouldConvertImageToByteArray() throws IOException {
    // When
    var result = imageService.convertToByteArray(testImage);

    // Then
    assertNotNull(result);
    assertTrue(result.length > 0);

    // Verify the byte array can be converted back to an image
    var reconstructedImage = new Image(new ByteArrayInputStream(result));
    assertEquals(TEST_IMAGE_WIDTH, (int) reconstructedImage.getWidth());
    assertEquals(TEST_IMAGE_HEIGHT, (int) reconstructedImage.getHeight());
  }

  @Test
  void convertToJavaFxImage_ShouldConvertByteArrayToImage() throws IOException {
    // Given
    var imageBytes = imageService.convertToByteArray(testImage);

    // When
    var result = imageService.convertToJavaFxImage(imageBytes);

    // Then
    assertNotNull(result);
    assertEquals(TEST_IMAGE_WIDTH, (int) result.getWidth());
    assertEquals(TEST_IMAGE_HEIGHT, (int) result.getHeight());
  }

  @Test
  void convertToByteArray_ShouldThrowIOException_WhenImageIsNull() {
    // Then
    assertThrows(NullPointerException.class, () -> imageService.convertToByteArray(null));
  }

  @Test
  void convertToJavaFxImage_ShouldThrowException_WhenBytesAreNull() {
    // Then
    assertThrows(NullPointerException.class, () -> imageService.convertToJavaFxImage(null));
  }

  @Test
  void roundTripConversion_ShouldPreserveImageData() throws IOException {
    // Given
    var imageBytes = imageService.convertToByteArray(testImage);

    // When
    var result = imageService.convertToJavaFxImage(imageBytes);

    // Then
    assertNotNull(result);
    assertEquals(TEST_IMAGE_WIDTH, (int) result.getWidth());
    assertEquals(TEST_IMAGE_HEIGHT, (int) result.getHeight());

    // Convert back to byte array to complete the round trip
    var resultBytes = imageService.convertToByteArray(result);
    assertNotNull(resultBytes);
    assertTrue(resultBytes.length > 0);
  }
}
