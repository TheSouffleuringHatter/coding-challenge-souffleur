package windowskeyboardhook;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for running the Windows message loop. This service manages a separate thread
 * for the message loop and provides methods to start and stop the loop.
 */
@ApplicationScoped
class WindowsMessageLoopService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WindowsMessageLoopService.class);

  private final ExecutorService messageLoopExecutor;
  private final AtomicBoolean isRunning;
  private final CountDownLatch shutdownLatch;
  private final CountDownLatch hookInstalledLatch;
  private final KeyboardHookManager hookManager;

  @Inject
  WindowsMessageLoopService(final KeyboardHookManager hookManager) {
    this.hookManager = hookManager;
    this.messageLoopExecutor = Executors.newSingleThreadExecutor();
    this.isRunning = new AtomicBoolean(false);
    this.shutdownLatch = new CountDownLatch(1);
    this.hookInstalledLatch = new CountDownLatch(1);
  }

  /**
   * Starts the Windows message loop in a separate thread and installs the hook.
   *
   * @param keyboardProc The keyboard procedure to install
   * @return true if the message loop was started and hook installed successfully
   */
  boolean startMessageLoop(final LowLevelKeyboardProc keyboardProc) {
    if (isRunning.get()) {
      LOGGER.trace("Message loop is already running");
      return true;
    }

    isRunning.set(true);
    messageLoopExecutor.execute(() -> runMessageLoop(keyboardProc));

    // Wait for the hook to be installed before returning
    try {
      if (hookInstalledLatch.await(5, TimeUnit.SECONDS)) {
        LOGGER.debug("Message loop thread started and hook installed");
        return true;
      } else {
        LOGGER.warn("Hook installation timed out");
        isRunning.set(false);
        return false;
      }
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      isRunning.set(false);
      return false;
    }
  }

  /**
   * Stops the Windows message loop and waits for it to terminate.
   *
   * @param timeoutSeconds The maximum time to wait for the message loop to terminate
   * @return true if the message loop was stopped successfully, false otherwise
   */
  boolean stopMessageLoop(final int timeoutSeconds) {
    if (!isRunning.get()) {
      LOGGER.debug("Message loop is not running");
      return true;
    }

    LOGGER.debug("Stopping message loop...");
    isRunning.set(false);

    try {
      // Wait for the message loop to signal it has exited
      if (shutdownLatch.await(timeoutSeconds, TimeUnit.SECONDS)) {
        LOGGER.debug("Message loop terminated gracefully");
        return true;
      } else {
        LOGGER.warn("Message loop did not terminate gracefully within timeout");
        return false;
      }
    } catch (final InterruptedException e) {
      LOGGER.warn("Interrupted while waiting for message loop termination", e);
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Shuts down the message loop executor service.
   *
   * @param timeoutSeconds The maximum time to wait for the executor to terminate
   * @return true if the executor was shut down successfully, false otherwise
   */
  boolean shutdownExecutor(final int timeoutSeconds) {
    try {
      messageLoopExecutor.shutdown();
      if (!messageLoopExecutor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
        LOGGER.warn("Executor did not shutdown gracefully");
        messageLoopExecutor.shutdownNow();
        return messageLoopExecutor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
      }
      return true;
    } catch (final InterruptedException e) {
      LOGGER.warn("Interrupted while waiting for executor termination", e);
      messageLoopExecutor.shutdownNow();
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Checks if the message loop is currently running.
   *
   * @return true if the message loop is running, false otherwise
   */
  boolean isRunning() {
    return isRunning.get();
  }

  /** The actual message loop that runs in a separate thread and installs the hook. */
  private void runMessageLoop(final LowLevelKeyboardProc keyboardProc) {
    LOGGER.trace("Starting Windows message loop and installing hook");
    var msg = new MSG();

    try {
      // Use the hook manager to install the hook from within the message loop thread
      if (!hookManager.installKeyboardHook(keyboardProc)) {
        LOGGER.error("Failed to install keyboard hook via hook manager");
        hookInstalledLatch.countDown();
        return;
      }

      LOGGER.debug("Keyboard hook installed successfully from message loop thread");
      hookInstalledLatch.countDown();

      while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
        if (User32.INSTANCE.PeekMessage(msg, null, 0, 0, 1)) {
          LOGGER.trace("Processing window message: {}", msg.message);

          if (msg.message == WinUser.WM_QUIT) {
            LOGGER.debug("WM_QUIT message received, exiting loop");
            return;
          }

          User32.INSTANCE.TranslateMessage(msg);
          User32.INSTANCE.DispatchMessage(msg);
        } else {
          try {
            Thread.sleep(50);
          } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    } finally {
      // Uninstall the hook via the hook manager from the same thread
      hookManager.uninstallKeyboardHook();
      hookInstalledLatch.countDown(); // In case it wasn't counted down yet
      shutdownLatch.countDown();
      LOGGER.trace("Message loop terminated gracefully");
    }
  }
}
