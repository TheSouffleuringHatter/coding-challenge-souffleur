package dev.coding_challenge_souffleur.view.keylistener;

import static java.util.stream.Collectors.toUnmodifiableSet;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.PlatformRunLater;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import windowskeyboardhook.WindowsKeyEvent;
import windowskeyboardhook.WindowsKeyListener;

@ApplicationScoped
class KeyHandlerProcessor implements WindowsKeyListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyHandlerProcessor.class);

  private final PlatformRunLater platformRunLater;
  private final Map<Win32VK, KeyCommand> keyCommands;
  private final Set<Integer> modifierKeyCodes;
  private final KeyCommandDependencies keyCommandDependencies;

  @Inject
  KeyHandlerProcessor(
      final PlatformRunLater platformRunLater,
      @ConfigProperty(name = "app.keyboard.modifier.keys") final List<Win32VK> modifierKeys,
      final KeyCommandDependencies keyCommandDependencies,
      final Config config) {
    this.platformRunLater = platformRunLater;
    this.modifierKeyCodes =
        modifierKeys.stream().map(win32vk -> win32vk.code).collect(toUnmodifiableSet());
    this.keyCommandDependencies = keyCommandDependencies;

    keyCommands = new EnumMap<>(Win32VK.class);
    for (var command : KeyCommand.values()) {
      var keyCode = config.getValue(command.getKeyConfigProperty(), Win32VK.class);
      keyCommands.put(keyCode, command);
    }

    LOGGER.debug(
        "Initialized with {} key commands, modifier keys is: {}", keyCommands.size(), modifierKeys);
  }

  @Override
  public boolean responsibleFor(final WindowsKeyEvent event) {
    var pressedModifiers = event.pressedModifierKeyCodes();
    var modifierMatches = pressedModifiers.stream().anyMatch(modifierKeyCodes::contains);
    return modifierMatches && keyCommands.containsKey(event.keyCode());
  }

  @Override
  public boolean consume(final WindowsKeyEvent event) {
    var keyCommand = keyCommands.get(event.keyCode());
    if (keyCommand == null) {
      return false;
    }

    var commandName = keyCommand.name();
    LOGGER.trace("Received event {} for command {}", event, commandName);

    if (event.keyDown()) {
      LOGGER.trace("Executing command: {}", commandName);
      platformRunLater.accept(() -> keyCommand.execute(keyCommandDependencies));
    }

    // "true" for keyUp, too, for having the keyEvent not propagated further
    return true;
  }
}
