package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;
import dev.coding_challenge_souffleur.view.ViewController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Exit implements KeyHandler {

  public static final Win32VK EXIT_KEY_CODE = Win32VK.VK_Q;

  private final ViewController viewController;

  @Inject
  Exit(final ViewController viewController) {
    this.viewController = viewController;
  }

  @Override
  public void performAction() {
    viewController.exit();
  }

  @Override
  public Win32VK getKeyCode() {
    return EXIT_KEY_CODE;
  }
}
