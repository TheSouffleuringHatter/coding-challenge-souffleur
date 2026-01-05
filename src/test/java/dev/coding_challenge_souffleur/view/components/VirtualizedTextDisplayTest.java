package dev.coding_challenge_souffleur.view.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(ApplicationExtension.class)
@Isolated
class VirtualizedTextDisplayTest {

  private VirtualizedTextDisplay display;

  @Start
  void start(final Stage stage) {
    display = new VirtualizedTextDisplay();
    stage.setScene(new Scene(display, 400, 300));
    stage.show();
  }

  @Test
  void testSetFormattedContent() throws TimeoutException {
    var testContent = "This is a test\nWith multiple lines\nOf text content";

    Platform.runLater(() -> display.setFormattedContent(testContent));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertEquals(testContent, codeArea.getText());
    assertTrue(codeArea.getStyleClass().contains("text-content"));
    assertFalse(codeArea.getStyleClass().contains("code-text"));
  }

  @Test
  void testSetFormattedCodeContent() throws TimeoutException {
    var testCode = "public class Test {\n  public static void main(String[] args) {\n  }\n}";

    Platform.runLater(() -> display.setFormattedCodeContent(testCode));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertEquals(testCode, codeArea.getText());
    assertTrue(codeArea.getStyleClass().contains("code-text"));
    assertFalse(codeArea.getStyleClass().contains("text-content"));
    assertNotNull(codeArea.getParagraphGraphicFactory()); // Line numbers enabled
  }

  @Test
  void testDisplayProblemStatement() {
    var problemStatement = "Given an array of integers, find two numbers that add up to a target.";

    Platform.runLater(() -> display.displayProblemStatement(problemStatement));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertEquals(problemStatement, codeArea.getText());
  }

  @Test
  void testDisplayProblemStatementWithNull() {
    Platform.runLater(() -> display.displayProblemStatement(null));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertEquals("Loading...", codeArea.getText());
  }

  @Test
  void testDisplayProblemStatementWithEmptyString() {
    Platform.runLater(() -> display.displayProblemStatement(""));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertEquals("Loading...", codeArea.getText());
  }

  @Test
  void testSwitchingBetweenTextAndCodeMode() {
    // Start with text content
    Platform.runLater(() -> display.setFormattedContent("Regular text"));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertTrue(codeArea.getStyleClass().contains("text-content"));

    // Switch to code content
    Platform.runLater(() -> display.setFormattedCodeContent("int x = 42;"));
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(codeArea.getStyleClass().contains("code-text"));
    assertFalse(codeArea.getStyleClass().contains("text-content"));

    // Switch back to text content
    Platform.runLater(() -> display.setFormattedContent("Back to text"));
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(codeArea.getStyleClass().contains("text-content"));
    assertFalse(codeArea.getStyleClass().contains("code-text"));
  }

  @Test
  void testEmptyContent() {
    Platform.runLater(() -> display.setFormattedContent(""));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertTrue(codeArea.getText().isEmpty());
  }

  @Test
  void testNullContent() {
    Platform.runLater(() -> display.setFormattedContent(null));
    WaitForAsyncUtils.waitForFxEvents();

    var codeArea = getCodeArea();
    assertTrue(codeArea.getText().isEmpty());
  }

  private CodeArea getCodeArea() {
    var node = display.getChildren().getFirst();
    assertNotNull(node, "VirtualizedTextDisplay should have a child node");
    assertInstanceOf(CodeArea.class, node, "Child should be a CodeArea");
    return (CodeArea) node;
  }
}
