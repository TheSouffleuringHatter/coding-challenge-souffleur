package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import windowskeyboardhook.WindowsKeyEvent;
import windowskeyboardhook.WindowsKeyListener;

@ApplicationScoped
class KeyHandlerProcessor implements WindowsKeyListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyHandlerProcessor.class);
  private final PlatformRunLater platformRunLater;
  private final Map<Win32VK, KeyHandler> keyHandlers;
  private final List<Win32VK> modifierKeys;

  @Inject
  KeyHandlerProcessor(
      @Any final Instance<KeyHandler> keyHandlerInstances,
      final PlatformRunLater platformRunLater,
      @ConfigProperty(name = "app.keyboard.modifier.keys") final List<Win32VK> modifierKeys) {
    this.platformRunLater = platformRunLater;
    this.modifierKeys = modifierKeys;

    keyHandlers = new EnumMap<>(Win32VK.class);
    keyHandlerInstances.forEach(keyHandler -> keyHandlers.put(keyHandler.getKeyCode(), keyHandler));
    LOGGER.debug(
        "Initialized with {} key handlers, modifier keys is: {}", keyHandlers.size(), modifierKeys);
  }

  @Override
  public boolean responsibleFor(final WindowsKeyEvent event) {
    var pressedModifiers = event.pressedModifierKeyCodes();
    var configuredKeyCodes = modifierKeys.stream().map(win32vk -> win32vk.code).toList();
    var modifierMatches = pressedModifiers.stream().anyMatch(configuredKeyCodes::contains);
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
      LOGGER.trace("Executing action in: {}", keyHandlerClassSimpleName);
      platformRunLater.accept(keyHandler::performAction);
    }

    // "true" for keyUp, too, for having the keyEvent not propagated further
    return true;
  }
}
