package dev.coding_challenge_souffleur.view.components;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A custom TextFlow control that provides formatted content display with support for code styling
 * and line numbers. Enhanced with domain-specific methods for problem statement display.
 */
public class FormattedTextFlow extends TextFlow {

  private static final String CODE_STYLE = "code-text";

  /**
   * Updates the TextFlow with normal formatted content (no code styling).
   *
   * @param content The content to display
   */
  public void setFormattedContent(final String content) {
    setFormattedContentInternal(content, false);
  }

  /**
   * Updates the TextFlow with code formatted content (code styling and line numbers).
   *
   * @param content The content to display
   */
  public void setFormattedCodeContent(final String content) {
    setFormattedContentInternal(content, true);
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

  // Internal helper to keep logic in one place
  private void setFormattedContentInternal(final String content, final boolean isCode) {
    // Clear existing content
    this.getChildren().clear();

    // Handle null/empty content
    if (content == null || content.isEmpty()) {
      return;
    }

    // Process and add the content
    var lines = content.split("\\R");

    // Calculate the width needed for line numbers (e.g., " 23 " and "123 ")
    var lineNumberWidth = String.valueOf(lines.length).length();
    var lineNumberFormat = "%" + lineNumberWidth + "d ";

    // Add each line to the TextFlow
    var lineNumber = 1;
    for (var i = 0; i < lines.length; i++) {
      var line = lines[i];
      if (line.trim().isEmpty()) {
        continue;
      }

      var lineNode = new Text(line);
      if (isCode) {
        // Add line number for code
        var lineNumberText = new Text(String.format(lineNumberFormat, lineNumber));
        lineNumberText.getStyleClass().addAll(CODE_STYLE, "line-number");
        this.getChildren().add(lineNumberText);

        // Apply code styling to the line
        lineNode.getStyleClass().add(CODE_STYLE);
      }

      this.getChildren().add(lineNode);

      if (i < lines.length - 1) {
        this.getChildren().add(new Text("\n"));
      }

      ++lineNumber;
    }
  }
}
