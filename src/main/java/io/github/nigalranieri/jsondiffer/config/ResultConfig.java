package io.github.nigalranieri.jsondiffer.config;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import io.github.nigalranieri.jsondiffer.result.DifferenceValueType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ResultConfig {

  private List<DifferenceType> types = new ArrayList<>();
  private String valueMismatchPattern;

  /**
   * Returns the difference types retained by result filtering.
   *
   * <p>An empty list means that no difference-type filtering is applied.
   *
   * @return the retained difference types
   */
  public List<DifferenceType> getTypes() {
    return Collections.unmodifiableList(types);
  }

  /**
   * Configures the difference types retained by result filtering.
   *
   * <p>A {@code null} value is normalized to an empty list. An empty list means that all difference
   * types remain eligible.
   *
   * @param types the difference types to retain
   */
  public void setTypes(List<DifferenceType> types) {
    this.types = types == null ? new ArrayList<DifferenceType>() : new ArrayList<>(types);
  }

  /**
   * Returns the regular expression used to filter value mismatches.
   *
   * @return the configured pattern, or {@code null} when no pattern is configured
   */
  public String getValueMismatchPattern() {
    return valueMismatchPattern;
  }

  /**
   * Configures the regular expression used to filter value mismatches.
   *
   * <p>The pattern applies only to {@link DifferenceType#VALUE_MISMATCH} differences. Other
   * retained difference types are unaffected.
   *
   * @param valueMismatchPattern the regular expression, or {@code null} to disable regex filtering
   */
  public void setValueMismatchPattern(String valueMismatchPattern) {
    this.valueMismatchPattern = valueMismatchPattern;
  }

  /**
   * Applies this result configuration to the supplied comparison result.
   *
   * <p>Configured {@code types} are inclusive. When no types are configured, all difference types
   * remain eligible. A configured value-mismatch pattern further restricts only {@link
   * DifferenceType#VALUE_MISMATCH} differences and does not remove other retained types.
   *
   * @param result the comparison result to filter
   * @return a new comparison result containing only retained differences
   * @throws NullPointerException if {@code result} is {@code null}
   * @throws java.util.regex.PatternSyntaxException if the configured pattern is invalid
   */
  public ComparisonResult apply(ComparisonResult result) {
    Objects.requireNonNull(result, "result");

    Pattern pattern = valueMismatchPattern == null ? null : Pattern.compile(valueMismatchPattern);

    List<Difference> filtered = new ArrayList<>();

    for (Difference difference : result.getDifferences()) {
      if (!types.isEmpty() && !types.contains(difference.getType())) {
        continue;
      }

      if (pattern != null
          && difference.getType() == DifferenceType.VALUE_MISMATCH
          && !matchesPattern(difference.getExpected(), pattern)
          && !matchesPattern(difference.getActual(), pattern)) {
        continue;
      }

      filtered.add(difference);
    }

    return new ComparisonResult(filtered);
  }

  private boolean matchesPattern(DifferenceValue value, Pattern pattern) {
    return value.getType() == DifferenceValueType.STRING
        && pattern.matcher((String) value.getValue()).matches();
  }
}
