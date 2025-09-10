package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ApplicationExitService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
class Exit implements KeyHandler {

  private final ApplicationExitService applicationExitService;

  @Inject
  Exit(final ApplicationExitService applicationExitService) {
    this.applicationExitService = applicationExitService;
  }

  @Override
  public void performAction() {
    applicationExitService.exitApplication();
  }

  @Override
  public Win32VK getKeyCode() {
    return KeyConstants.EXIT_KEY_CODE;
  }
}
