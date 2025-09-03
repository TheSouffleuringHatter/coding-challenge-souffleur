package dev.coding_challenge_souffleur.model;

import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Enum representing different sections that can be extracted from the response. */
enum AnalysisResultSection {
  PROBLEM_STATEMENT {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setProblemStatement(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getProblemStatement();
    }
  },

  SOLUTION_TITLE {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setSolutionTitle(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getSolutionTitle();
    }
  },

  SOLUTION_DESCRIPTION {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setSolutionDescription(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getSolutionDescription();
    }
  },

  EDGE_CASES {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setEdgeCases(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getEdgeCases();
    }
  },

  SOLUTION_CODE {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setSolutionCode(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getSolutionCode();
    }
  },

  TIME_COMPLEXITY {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setTimeComplexity(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getTimeComplexity();
    }
  },

  SPACE_COMPLEXITY {
    @Override
    void setValue(final StreamingAnalysisResult result, final String value) {
      result.setSpaceComplexity(value);
    }

    @Override
    Optional<String> getValue(final StreamingAnalysisResult result) {
      return result.getSpaceComplexity();
    }
  };

  private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisResultSection.class);
  private static final String SECTION_END = "===SECTION_END===";
  private final Pattern pattern;
  private final Pattern partialPattern;

  AnalysisResultSection() {
    this.pattern = Pattern.compile(this.name() + ":(.*?)" + SECTION_END, Pattern.DOTALL);
    this.partialPattern =
        Pattern.compile(this.name() + ":(.*?)(?=" + SECTION_END + "|$)", Pattern.DOTALL);
  }

  abstract void setValue(final StreamingAnalysisResult result, final String value);

  abstract Optional<String> getValue(final StreamingAnalysisResult result);

  boolean extractAndUpdate(final String text, final StreamingAnalysisResult result) {
    // First check for complete section
    var completeMatcher = pattern.matcher(text);
    if (completeMatcher.find()) {
      var extractedValue = completeMatcher.group(1).trim();
      var currentValue = getValue(result).orElse("");

      if (!extractedValue.equals(currentValue)) {
        LOGGER.debug("Found complete {} in text", this.name());
        setValue(result, extractedValue);
        return true;
      }

      return false; // Complete section already extracted
    }

    // If no complete section, check for partial content
    var partialMatcher = partialPattern.matcher(text);
    if (partialMatcher.find()) {
      var partialContent = partialMatcher.group(1).trim();
      var currentValue = getValue(result).orElse("");

      if (!partialContent.isEmpty() && !partialContent.equals(currentValue)) {
        LOGGER.trace("Found partial {} content in text", this.name());
        setValue(result, partialContent);
        return true;
      }
    }

    return false;
  }

  static boolean containsSolutionContent(final String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }
    for (final var section : AnalysisResultSection.values()) {
      if (section == AnalysisResultSection.PROBLEM_STATEMENT) {
        continue;
      }
      if (text.contains(section.name() + ":")) {
        return true;
      }
    }
    return false;
  }
}
