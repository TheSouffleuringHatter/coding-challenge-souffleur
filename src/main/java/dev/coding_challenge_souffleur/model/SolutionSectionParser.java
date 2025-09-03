package dev.coding_challenge_souffleur.model;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for parsing text and extracting section contents. Separates parsing
 * concerns from data container classes.
 */
public final class SolutionSectionParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(SolutionSectionParser.class);

  private SolutionSectionParser() {}

  private static Pattern completePattern(final SolutionSection solutionSection) {
    return Pattern.compile(
        solutionSection.name() + ":(.*?)" + SolutionSection.SECTION_END, Pattern.DOTALL);
  }

  private static Pattern partialPattern(final SolutionSection solutionSection) {
    return Pattern.compile(
        solutionSection.name() + ":(.*?)(?=" + SolutionSection.SECTION_END + "|$)",
        Pattern.DOTALL);
  }

  /**
   * Attempts to extract the content for the provided section from text and update the result.
   * Returns true if the result was updated (complete or partial content changed), otherwise false.
   */
  public static boolean extractAndUpdate(
      final String text,
      final SolutionSection solutionSection,
      final StreamingAnalysisResult result) {
    if (text == null || text.isEmpty() || solutionSection == null || result == null) {
      return false;
    }

    // First check for complete section
    var completeMatcher = completePattern(solutionSection).matcher(text);
    if (completeMatcher.find()) {
      var extractedValue = completeMatcher.group(1).trim();
      var currentValue = result.getSection(solutionSection).orElse("");

      if (!extractedValue.equals(currentValue)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Found complete {} in text", solutionSection.name());
        }
        result.setSection(solutionSection, extractedValue);
        return true;
      }

      return false; // Complete section already extracted
    }

    // If no complete section, check for partial content
    var partialMatcher = partialPattern(solutionSection).matcher(text);
    if (partialMatcher.find()) {
      var partialContent = partialMatcher.group(1).trim();
      var currentValue = result.getSection(solutionSection).orElse("");

      if (!partialContent.isEmpty() && !partialContent.equals(currentValue)) {
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Found partial {} content in text", solutionSection.name());
        }
        result.setSection(solutionSection, partialContent);
        return true;
      }
    }

    return false;
  }
}
