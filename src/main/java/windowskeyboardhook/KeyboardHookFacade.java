package windowskeyboardhook;

import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for the keyboard hook functionality. This class coordinates the other components and
 * provides a simple interface for the application.
 */
@ApplicationScoped
public class KeyboardHookFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardHookFacade.class);
  private static final int MESSAGE_LOOP_TIMEOUT = 2;

  private final KeyboardHookManager hookManager;
  private final WindowsMessageLoopService messageLoopService;
  private final KeyboardEventProcessor eventProcessor;

  @Inject
  KeyboardHookFacade(
      final KeyboardHookManager hookManager,
      final WindowsMessageLoopService messageLoopService,
      final KeyboardEventProcessor eventProcessor) {
    this.hookManager = hookManager;
    this.messageLoopService = messageLoopService;
    this.eventProcessor = eventProcessor;
  }

  /**
   * Validates that the application is running on a Windows platform. Supports Windows 10, Windows
   * 11, and future Windows versions.
   *
   * @throws UnsupportedOperationException if not running on a supported Windows version
   */
  private static void validateWindowsPlatform() {
    if (!SystemUtils.IS_OS_WINDOWS) {
      LOGGER.warn("Keyboard hook requires Windows platform, current OS: {}", SystemUtils.OS_NAME);
      throw new UnsupportedOperationException("Windows platform required");
    }

    // Log the Windows version for informational purposes
    LOGGER.trace("Running on Windows version: {}", SystemUtils.OS_VERSION);
  }

  /**
   * Initializes and starts the keyboard hook when the CDI container is initialized.
   *
   * @param containerInitialized The container initialized event
   */
  void registerAndBind(
      @Observes @Initialized(ApplicationScoped.class)
          final ContainerInitialized containerInitialized) {
    LOGGER.trace("Container initialized: {}", containerInitialized);

    validateWindowsPlatform();

    LowLevelKeyboardProc keyboardProc = eventProcessor::handleKeyboardEvent;

    // Now the message loop service handles both the loop AND hook installation
    if (!messageLoopService.startMessageLoop(keyboardProc)) {
      LOGGER.error("Failed to start message loop and install keyboard hook");
      return;
    }

    LOGGER.info("Keyboard hook system started successfully");
  }

  @PreDestroy
  void cleanup() {
    LOGGER.debug("Cleaning up keyboard hook resources...");

    if (messageLoopService.stopMessageLoop(3)) {
      LOGGER.debug("Message loop stopped successfully");
    } else {
      LOGGER.warn("Failed to stop message loop gracefully");
    }

    if (hookManager.uninstallKeyboardHook()) {
      LOGGER.debug("Keyboard hook uninstalled successfully");
    } else {
      LOGGER.warn("Failed to uninstall keyboard hook");
    }

    if (messageLoopService.shutdownExecutor(MESSAGE_LOOP_TIMEOUT)) {
      LOGGER.debug("Message loop executor shut down successfully");
    } else {
      LOGGER.warn("Failed to shut down message loop executor");
    }

    LOGGER.debug("Cleanup completed");
  }

  /**
   * Checks if the keyboard hook is running.
   *
   * @return true if the keyboard hook is running, false otherwise
   */
  public boolean isRunning() {
    return hookManager.isHookInstalled() && messageLoopService.isRunning();
  }
}
