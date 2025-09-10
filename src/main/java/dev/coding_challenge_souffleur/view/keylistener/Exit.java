package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
class Exit implements KeyHandler {

  private final Runnable exitApplication;

  @Inject
  Exit(@Named("exitApplication") final Runnable exitApplication) {
    this.exitApplication = exitApplication;
  }

  @Override
  public void performAction() {
    exitApplication.run();
  }

  @Override
  public Win32VK getKeyCode() {
    return KeyConstants.EXIT_KEY_CODE;
  }
}
