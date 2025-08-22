package dev.coding_challenge_souffleur.model;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/** Service for image conversion operations. */
@ApplicationScoped
class ImageService {

  /** The image format used for conversions. */
  static final String IMAGE_FORMAT = "png";

  /**
   * Converts a JavaFX Image to a byte array.
   *
   * @param fxImage the JavaFX Image to convert
   * @return the image as a byte array
   * @throws IOException if an error occurs during conversion
   */
  byte[] convertToByteArray(final Image fxImage) throws IOException {
    var bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
    try (var outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(bufferedImage, IMAGE_FORMAT, outputStream);
      return outputStream.toByteArray();
    }
  }

  /**
   * Converts a byte array to a JavaFX Image.
   *
   * @param imageBytes the byte array to convert
   * @return the JavaFX Image
   */
  Image convertToJavaFxImage(final byte[] imageBytes) {
    return new Image(new ByteArrayInputStream(imageBytes));
  }
}
