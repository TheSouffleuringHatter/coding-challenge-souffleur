package dev.coding_challenge_souffleur.view.components;

import javafx.scene.layout.StackPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * A memory-efficient text display component using RichTextFX's virtualized rendering. This
 * component replaces FormattedTextFlow's node-per-line approach with virtualized scrolling, which
 * only renders visible lines in the viewport.
 */
public class VirtualizedTextDisplay extends StackPane {

  private static final String CODE_STYLE_CLASS = "code-text";
  private static final String TEXT_STYLE_CLASS = "text-content";

  private final CodeArea codeArea;
  private boolean isCodeMode = false;

  public VirtualizedTextDisplay() {
    this.codeArea = new CodeArea();
    this.codeArea.setEditable(false);
    this.codeArea.setWrapText(true); // Enable wrapping by default for text sections

    // Apply default text styling
    this.codeArea.getStyleClass().add(TEXT_STYLE_CLASS);

    this.getChildren().add(codeArea);
  }

  /**
   * Updates the display with normal formatted content (no code styling or line numbers).
   *
   * @param content The content to display
   */
  public void setFormattedContent(final String content) {
    setContentInternal(content, false);
  }

  /**
   * Updates the display with code formatted content (code styling and line numbers).
   *
   * @param content The content to display
   */
  public void setFormattedCodeContent(final String content) {
    setContentInternal(content, true);
  }

  /**
   * Displays problem statement content with appropriate formatting. This method provides a specific
   * interface for problem statement display.
   *
   * @param problemStatement The problem statement content to display
   */
  public void displayProblemStatement(final String problemStatement) {
    if (problemStatement == null || problemStatement.isEmpty()) {
      setFormattedContent("Loading...");
    } else {
      setFormattedContent(problemStatement);
    }
  }

  private void setContentInternal(final String content, final boolean isCode) {
    // Handle null/empty content
    if (content == null || content.isEmpty()) {
      codeArea.clear();
      return;
    }

    // Update content
    codeArea.replaceText(content);

    // Update styling and line numbers based on mode
    if (isCode != isCodeMode) {
      isCodeMode = isCode;
      updateStyling();
    }

    // Resize CodeArea to fit all content
    resizeToFitContent();
  }

  private void resizeToFitContent() {
    // Calculate height needed to show all paragraphs (lines)
    var paragraphCount = codeArea.getParagraphs().size();

    // More accurate line height based on CSS font-size: 13px
    // Actual rendered height is closer to font-size + minimal spacing
    // Using 16px (13px font + 3px spacing) for accurate tight layout
    var estimatedLineHeight = 16.0;
    var totalHeight = paragraphCount * estimatedLineHeight;

    // Set height on both CodeArea AND StackPane wrapper to prevent extra space
    codeArea.setPrefHeight(totalHeight);
    codeArea.setMinHeight(totalHeight);
    this.setPrefHeight(totalHeight);
    this.setMinHeight(totalHeight);
  }

  private void updateStyling() {
    if (isCodeMode) {
      // Enable line numbers for code
      codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

      // Apply code styling
      codeArea.getStyleClass().remove(TEXT_STYLE_CLASS);
      codeArea.getStyleClass().add(CODE_STYLE_CLASS);
    } else {
      // Disable line numbers for regular text
      codeArea.setParagraphGraphicFactory(null);

      // Apply text styling
      codeArea.getStyleClass().remove(CODE_STYLE_CLASS);
      codeArea.getStyleClass().add(TEXT_STYLE_CLASS);
    }
  }
}
