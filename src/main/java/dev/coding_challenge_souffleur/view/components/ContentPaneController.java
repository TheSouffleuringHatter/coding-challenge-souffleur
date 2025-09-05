package dev.coding_challenge_souffleur.view.components;

import jakarta.enterprise.context.ApplicationScoped;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service responsible for content pane visibility and window sizing logic. */
@ApplicationScoped
public class ContentPaneController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ContentPaneController.class);
  private static final double WINDOW_WIDTH_RATIO = 0.3;

  private static void adjustWindowToScreen(final Window window) {
    var screenBounds = Screen.getPrimary().getVisualBounds();
    window.setWidth(screenBounds.getWidth() * WINDOW_WIDTH_RATIO);
    window.setHeight(screenBounds.getHeight());
    window.setY(screenBounds.getMinY());
  }

  /**
   * Shows the content pane if it's not currently visible.
   *
   * @param contentPane the content pane to show
   */
  public void showContentPane(final VBox contentPane) {
    LOGGER.trace("Content pane visible: {}", contentPane.isVisible());
    if (!contentPane.isVisible()) {
      LOGGER.trace("Making content pane visible...");
      contentPane.setVisible(true);
      contentPane.setManaged(true);
      LOGGER.debug("Made content pane visible");
    }
  }

  /**
   * Adjusts the window size to fit the screen dimensions.
   *
   * @param window the window to adjust
   */
  public void adjustWindowSize(final Window window) {
    if (window != null) {
      adjustWindowToScreen(window);
      LOGGER.trace("Window size adjusted");
    }
  }
}
