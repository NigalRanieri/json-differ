package io.github.nigalranieri.jsondiffer.config;

import io.github.nigalranieri.jsondiffer.result.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Configures filtering applied to comparison results.
 *
 * <p>Result filtering is applied after comparison and determines which detected differences are
 * retained in the {@link ComparisonResult}. It does not affect how differences are detected.
 *
 * <p>When {@link #getTypes()} is empty, all difference types are eligible to be retained. When one
 * or more types are configured, only those difference types are eligible.
 *
 * <p>A {@linkplain #getValueMismatchPattern() value-mismatch pattern}, when configured, further
 * filters {@link DifferenceType#VALUE_MISMATCH VALUE_MISMATCH} differences. A value mismatch is
 * retained when either its expected or actual string value matches the entire regular expression.
 * Non-string value mismatches do not match the pattern. Other eligible difference types are not
 * affected by the pattern.
 *
 * <p>When both difference types and a value-mismatch pattern are configured, the type filter first
 * determines which difference types are eligible, and the pattern then further restricts eligible
 * {@code VALUE_MISMATCH} differences.
 */
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
    return types;
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
