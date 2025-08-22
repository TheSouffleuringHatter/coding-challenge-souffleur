package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.JavaFxApplication;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import windowskeyboardhook.WindowsKeyEvent;
import windowskeyboardhook.WindowsKeyListener;

@ApplicationScoped
class KeyHandlerProcessor implements WindowsKeyListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyHandlerProcessor.class);
  private final PlatformRunLater platformRunLater;
  private final Map<Win32VK, KeyHandler> keyHandlers;

  @Inject
  KeyHandlerProcessor(
      @Any final Instance<KeyHandler> keyHandlerInstances,
      final PlatformRunLater platformRunLater) {
    this.platformRunLater = platformRunLater;

    keyHandlers = new EnumMap<>(Win32VK.class);
    keyHandlerInstances.forEach(keyHandler -> keyHandlers.put(keyHandler.getKeyCode(), keyHandler));
    LOGGER.debug("Initialized with {} key handlers", keyHandlers.size());
  }

  @Override
  public boolean responsibleFor(final WindowsKeyEvent event) {
    var modifierMatches =
        Boolean.getBoolean(JavaFxApplication.APPLICATION_TESTING_FLAG)
            ? event.anyShiftPressed()
            : event.modifierKeyCodesMatchExactly(MatchingModifier.MATCHING_MODIFIER);
    return modifierMatches && keyHandlers.containsKey(event.keyCode());
  }

  @Override
  public boolean consume(final WindowsKeyEvent event) {
    var keyHandler = keyHandlers.get(event.keyCode());
    if (keyHandler == null) {
      return false;
    }

    var keyHandlerClassSimpleName = keyHandler.getClass().getSimpleName();
    LOGGER.trace("Received event {} in {}", event, keyHandlerClassSimpleName);

    if (event.keyDown()) {
      LOGGER.debug("Executing action in: {}", keyHandlerClassSimpleName);
      platformRunLater.accept(keyHandler::performAction);
    }

    // "true" for keyUp, too, for having the keyEvent not propagated further
    return true;
  }
}
