package dev.coding_challenge_souffleur.model;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Parser bean responsible for extracting section contents from text. */
@ApplicationScoped
class SolutionSectionParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(SolutionSectionParser.class);

  private static Pattern completePattern(final SolutionSection solutionSection) {
    return solutionSection.completePattern();
  }

  private static Pattern partialPattern(final SolutionSection solutionSection) {
    return solutionSection.partialPattern();
  }

  /**
   * Returns the content of the given section from the provided text. Prefers complete section
   * content; if not present, falls back to the latest partial content. Returns empty if not found.
   */
  Optional<String> extractSectionContent(final String text, final SolutionSection solutionSection) {
    if (text == null || text.isEmpty() || solutionSection == null) {
      return Optional.empty();
    }

    var completeMatcher = completePattern(solutionSection).matcher(text);
    if (completeMatcher.find()) {
      return Optional.of(completeMatcher.group(1).trim());
    }

    var partialMatcher = partialPattern(solutionSection).matcher(text);
    if (partialMatcher.find()) {
      var partial = partialMatcher.group(1).trim();
      if (!partial.isEmpty()) {
        return Optional.of(partial);
      }
    }

    return Optional.empty();
  }

  /**
   * Attempts to extract the content for the provided section from text and update the result.
   * Returns true if the result was updated (complete or partial content changed), otherwise false.
   */
  boolean extractAndUpdate(
      final StreamingAnalysisResult result,
      final SolutionSection solutionSection,
      final String text) {
    if (text == null || text.isEmpty() || solutionSection == null || result == null) {
      return false;
    }

    var contentOpt = extractSectionContent(text, solutionSection);
    if (contentOpt.isPresent()) {
      var newValue = contentOpt.get();
      var currentValue = result.getSection(solutionSection).orElse("");
      if (!newValue.equals(currentValue)) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Updated {} from parsed content", solutionSection.name());
        }
        result.setSection(solutionSection, newValue);
        return true;
      }
    }

    return false;
  }
}
