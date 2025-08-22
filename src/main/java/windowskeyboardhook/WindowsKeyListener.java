package windowskeyboardhook;

/** Interface for handling Windows keyboard events. */
public interface WindowsKeyListener {

  boolean consume(final WindowsKeyEvent event);

  boolean responsibleFor(final WindowsKeyEvent event);
}
