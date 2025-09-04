package dev.coding_challenge_souffleur.model;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.W32APIOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Supplier;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service responsible for capturing screenshots and making them available as byte arrays. */
@ApplicationScoped
public class ScreenshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotService.class);
  private static final String DEFAULT_SCREENSHOT_FILE_NAME_PREFIX =
      "screenshot." + ImageService.IMAGE_FORMAT;

  private final ImageService imageService;
  private final boolean saveScreenshotToFile;
  private Image screenshot;

  @Inject
  public ScreenshotService(
      @ConfigProperty(name = "save.screenshot.to.file") final boolean saveScreenshotToFile,
      final ImageService imageService) {
    this.saveScreenshotToFile = saveScreenshotToFile;
    this.imageService = imageService;
  }

  private static Rectangle2D captureDesktopBounds() {
    var screenBounds = Screen.getPrimary().getBounds();

    // Use min/max coordinates to get full desktop area
    var minX = screenBounds.getMinX();
    var minY = screenBounds.getMinY();
    var maxX = screenBounds.getMaxX();
    var maxY = screenBounds.getMaxY();
    var width = maxX - minX;
    var height = maxY - minY;

    return new Rectangle2D(minX, minY, width, height);
  }

  /**
   * Gets the dimensions of the currently focused window.
   *
   * @return Optional containing the window dimensions, or empty if unable to get dimensions
   */
  private static Optional<Rectangle2D> getFocusedWindowBounds() {
    var hwnd = WinUserLibrary.INSTANCE.GetForegroundWindow();
    if (hwnd == null) {
      LOGGER.warn("Failed to get foreground window handle");
      return Optional.empty();
    }

    var rect = new RECT();
    var success = WinUserLibrary.INSTANCE.GetWindowRect(hwnd, rect);
    if (!success) {
      LOGGER.warn("Failed to get window dimensions");
      return Optional.empty();
    }

    // Get the DPI value for the window and calculate the scaling factor
    var dpi = WinUserLibrary.INSTANCE.GetDpiForWindow(hwnd);
    // Default DPI is 96, so calculate the scaling factor
    var dpiScale = dpi / 96.0;

    LOGGER.debug("Window DPI: {}, DPI scaling factor: {}", dpi, dpiScale);

    // Use exact integer values for the bounds to prevent sampling issues
    var x = Math.floor(rect.left / dpiScale);
    var y = Math.floor(rect.top / dpiScale);
    var width = Math.ceil((rect.right - rect.left) / dpiScale);
    var height = Math.ceil((rect.bottom - rect.top) / dpiScale);

    LOGGER.debug(
        "Original window bounds: left={}, top={}, right={}, bottom={}",
        rect.left,
        rect.top,
        rect.right,
        rect.bottom);
    LOGGER.debug(
        "DPI-adjusted window bounds: x={}, y={}, width={}, height={}", x, y, width, height);

    return Optional.of(new Rectangle2D(x, y, width, height));
  }

  private Image captureAndProcessScreenshot(final Supplier<Rectangle2D> boundsSupplier) {
    var bounds = boundsSupplier.get();
    var fxImage = captureScreenImage(bounds);

    saveScreenshotToFile(fxImage);
    screenshot = fxImage;

    return fxImage;
  }

  private Image captureScreenImage(final Rectangle2D bounds) {
    var robot = new Robot();
    LOGGER.debug(
        "Capturing screenshot: x={}, y={}, width={}, height={}",
        bounds.getMinX(),
        bounds.getMinY(),
        bounds.getWidth(),
        bounds.getHeight());

    // Ensure the coordinates and dimensions are whole numbers (not fractional)
    var x = (int) Math.floor(bounds.getMinX());
    var y = (int) Math.floor(bounds.getMinY());
    var width = (int) Math.ceil(bounds.getWidth());
    var height = (int) Math.ceil(bounds.getHeight());

    // Add a small delay to ensure the screen is ready for capture
    try {
      Thread.sleep(100);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return robot.getScreenCapture(null, x, y, width, height);
  }

  private void saveScreenshotToFile(final Image image) {
    if (!this.saveScreenshotToFile) {
      LOGGER.debug("Screenshot saving disabled, skipping save to file");
      return;
    }

    var timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    var outputPath =
        Path.of(
            DEFAULT_SCREENSHOT_FILE_NAME_PREFIX
                + "_"
                + timestamp
                + "."
                + ImageService.IMAGE_FORMAT);

    try {
      var imageBytes = imageService.convertToByteArray(image);
      Files.write(
          outputPath, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      LOGGER.debug("Screenshot saved successfully at: {}", outputPath.toAbsolutePath());
    } catch (final IOException e) {
      LOGGER.warn("Failed to save screenshot to file", e);
    }
  }

  /**
   * Takes a screenshot of the entire desktop (all screens) and stores it internally. Also saves the
   * screenshot to the default file location.
   */
  public Optional<Image> takeScreenshotOfDesktop() {
    var capturedScreenshot = captureAndProcessScreenshot(ScreenshotService::captureDesktopBounds);
    return Optional.ofNullable(capturedScreenshot);
  }

  /**
   * Takes a screenshot of the currently focused window and stores it internally. Also saves the
   * screenshot to the default file location. Falls back to capturing the entire primary screen if
   * unable to determine the focused window.
   */
  public Optional<Image> takeScreenshotOfForegroundWindow() {
    var focusedWindowBounds =
        getFocusedWindowBounds()
            .orElseGet(
                () -> {
                  LOGGER.debug("Falling back to full screen capture");
                  return Screen.getPrimary().getBounds();
                });
    var capturedScreenshot = captureAndProcessScreenshot(() -> focusedWindowBounds);

    return Optional.ofNullable(capturedScreenshot);
  }

  /**
   * Returns the last captured screenshot, if available.
   *
   * @return Optional containing the screenshot data, or empty if no screenshot available
   */
  public Optional<Image> getScreenshot() {
    return Optional.ofNullable(screenshot);
  }

  /**
   * Interface for required Windows User32 DLL functions. This interface provides access to Windows
   * API functions through JNA.
   */
  private interface WinUserLibrary extends User32 {

    WinUserLibrary INSTANCE =
        Native.load("user32", WinUserLibrary.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * Gets the DPI value for the specified window.
     *
     * @param hWnd Window handle
     * @return The DPI value for the window
     */
    int GetDpiForWindow(final HWND hWnd);
  }
}
