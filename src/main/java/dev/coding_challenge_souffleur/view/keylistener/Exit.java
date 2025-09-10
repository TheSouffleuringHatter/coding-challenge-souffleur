package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
class Exit implements KeyHandler {

  private final Runnable exitApplication;
  private final Win32VK exitKeyCode;

  @Inject
  Exit(
      @ConfigProperty(name = "app.keyboard.key.exit") final Win32VK exitKeyCode,
      @Named("exitApplication") final Runnable exitApplication) {
    this.exitApplication = exitApplication;
    this.exitKeyCode = exitKeyCode;
  }

  @Override
  public void performAction() {
    exitApplication.run();
  }

  @Override
  public Win32VK getKeyCode() {
    return exitKeyCode;
  }
}
