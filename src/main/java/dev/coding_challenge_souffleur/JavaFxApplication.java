package dev.coding_challenge_souffleur;

import com.sun.glass.ui.Window;
import dev.coding_challenge_souffleur.view.StageInitializer;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import windowskeyboardhook.KeyboardHookFacade;

public class JavaFxApplication extends Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaFxApplication.class);

  private WeldContainer weldContainer;

  private Weld weld;

  @Override
  public void init() {
    if (!SystemUtils.IS_OS_WINDOWS) {
      throw new UnsupportedOperationException("This application requires MS Windows.");
    }

    this.weld = new Weld().addPackages(true, this.getClass(), KeyboardHookFacade.class);
  }

  @Override
  public void start(final Stage unusedStage) {
    weldContainer = weld.initialize();

    // Enforcing eager loading of the stage
    weldContainer.select(StageInitializer.class).get().getStage();
  }

  @Override
  public void stop() {
    Window.getWindows().forEach(Window::close);

    if (weldContainer == null) {
      LOGGER.debug("Weld container is null");
      return;
    }
    weldContainer.shutdown();
  }

  WeldContainer getWeldContainer() {
    return weldContainer;
  }
}
