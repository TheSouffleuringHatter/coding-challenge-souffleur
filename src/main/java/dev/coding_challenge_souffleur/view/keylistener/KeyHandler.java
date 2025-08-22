package dev.coding_challenge_souffleur.view.keylistener;

import com.sun.jna.platform.win32.Win32VK;

interface KeyHandler {

  void performAction();

  Win32VK getKeyCode();
}
