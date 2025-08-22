package dev.coding_challenge_souffleur.view;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A custom TextFlow control that provides formatted content display with support for code styling
 * and line numbers.
 */
public class FormattedTextFlow extends TextFlow {

  private static final String CODE_STYLE = "code-text";

  /**
   * Updates the TextFlow with formatted content, handling null safety and line breaks.
   *
   * @param content The content to display
   * @param isCode Whether to apply code styling
   */
  void setFormattedContent(final String content, final boolean isCode) {
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
