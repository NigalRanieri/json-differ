package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.internal.PathTolerance;
import io.github.nigalranieri.jsondiffer.internal.path.PathValidator;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Builder for configuring JSON comparison behavior.
 *
 * <p>A builder can be used either for a one-off comparison through {@link #compare(String, String)}
 * or {@link #compare(Path, Path)}, or to create a reusable {@link JsonComparator} through {@link
 * #build()}.
 *
 * <p>All comparison options are disabled by default, resulting in strict structural comparison.
 */
public final class JsonCompareBuilder {

  private boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths = new HashSet<>();
  private boolean treatNullAndMissingAsEqual;
  private Double numericTolerance;
  private final Set<String> unorderedArrayPaths = new HashSet<>();
  private final List<PathTolerance> pathNumericTolerances = new ArrayList<>();
  private final Set<String> nullAndMissingEqualPaths = new HashSet<>();
  private boolean ignoreCase;
  private final Set<String> ignoreCasePaths = new HashSet<>();

  JsonCompareBuilder() {}

  /**
   * Ignores all differences at the specified JSON path.
   *
   * <p>Ignoring a path also ignores the entire subtree rooted at that path.
   *
   * <p>Path patterns support exact properties and array indices as well as wildcards, including
   * {@code *}, {@code [*]}, and recursive {@code **}.
   *
   * @param path the path or path pattern to ignore
   * @return this builder
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is invalid
   */
  public JsonCompareBuilder ignorePath(String path) {
    PathValidator.validate(path);
    ignoredPaths.add(path);
    return this;
  }

  /**
   * Configures all arrays to be compared without considering element order.
   *
   * <p>Duplicate elements remain significant. Each element must still have a corresponding match in
   * the other array.
   *
   * @return this builder
   */
  public JsonCompareBuilder ignoreArrayOrder() {
    this.ignoreArrayOrder = true;
    return this;
  }

  /**
   * Configures arrays matching the specified path to be compared without considering element order.
   *
   * <p>Arrays at other paths remain order-sensitive unless global unordered-array comparison is
   * enabled through {@link #ignoreArrayOrder()}.
   *
   * <p>The path supports the same wildcard syntax as {@link #ignorePath(String)}.
   *
   * @param path the path or path pattern identifying arrays whose order should be ignored
   * @return this builder
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is invalid
   */
  public JsonCompareBuilder ignoreArrayOrder(String path) {
    PathValidator.validate(path);
    unorderedArrayPaths.add(path);
    return this;
  }

  /**
   * Treats a JSON property whose value is {@code null} as equivalent to the same property being
   * absent.
   *
   * <p>This option applies to object properties. It does not make JSON {@code null} equivalent to
   * arbitrary non-null values.
   *
   * @return this builder
   */
  public JsonCompareBuilder treatNullAndMissingAsEqual() {
    this.treatNullAndMissingAsEqual = true;
    return this;
  }

  /**
   * Treats a JSON property whose value is {@code null} as equivalent to the same property being
   * absent when the property path matches the specified path or path pattern.
   *
   * <p>Properties at other paths remain strict unless global null/missing equivalence is enabled
   * through {@link #treatNullAndMissingAsEqual()}.
   *
   * <p>The path supports the same wildcard syntax as {@link #ignorePath(String)}.
   *
   * @param path the path or path pattern where null and missing should be treated as equal
   * @return this builder
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is invalid
   */
  public JsonCompareBuilder treatNullAndMissingAsEqual(String path) {
    PathValidator.validate(path);
    nullAndMissingEqualPaths.add(path);
    return this;
  }

  /**
   * Configures the maximum allowed absolute difference between numeric values for them to be
   * considered equal.
   *
   * <p>For example, with a tolerance of {@code 0.01}, values whose absolute difference is less than
   * or equal to {@code 0.01} are considered equal.
   *
   * @param tolerance the non-negative finite numeric tolerance
   * @return this builder
   * @throws IllegalArgumentException if {@code tolerance} is negative, NaN, or infinite
   */
  public JsonCompareBuilder numericTolerance(double tolerance) {
    if (Double.isNaN(tolerance) || Double.isInfinite(tolerance)) {
      throw new IllegalArgumentException("Numeric tolerance must be finite");
    }

    if (tolerance < 0) {
      throw new IllegalArgumentException("Numeric tolerance cannot be negative");
    }

    this.numericTolerance = tolerance;
    return this;
  }

  /**
   * Configures the maximum allowed absolute difference between numeric values at the specified JSON
   * path for them to be considered equal.
   *
   * <p>The path supports the same wildcard syntax as {@link #ignorePath(String)}.
   *
   * @param path the path or path pattern where the tolerance should apply
   * @param tolerance the non-negative finite numeric tolerance
   * @return this builder
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is invalid
   * @throws IllegalArgumentException if {@code tolerance} is negative, NaN, or infinite
   */
  public JsonCompareBuilder numericTolerance(String path, double tolerance) {
    PathValidator.validate(path);

    if (Double.isNaN(tolerance) || Double.isInfinite(tolerance)) {
      throw new IllegalArgumentException("Numeric tolerance must be finite");
    }
    if (tolerance < 0) {
      throw new IllegalArgumentException("Numeric tolerance cannot be negative");
    }

    pathNumericTolerances.add(new PathTolerance(path, tolerance));
    return this;
  }

  /**
   * Configures string values to be compared without considering case.
   *
   * <p>This option applies only to string values. Object field names remain case-sensitive.
   *
   * @return this builder
   */
  public JsonCompareBuilder ignoreCase() {
    this.ignoreCase = true;
    return this;
  }

  /**
   * Configures string values matching the specified path to be compared without considering case.
   *
   * <p>String values at other paths remain case-sensitive unless global case-insensitive comparison
   * is enabled through {@link #ignoreCase()}.
   *
   * <p>This option applies only to string values. Object field names remain case-sensitive.
   *
   * <p>The path supports the same wildcard syntax as {@link #ignorePath(String)}.
   *
   * @param path the path or path pattern where string case should be ignored
   * @return this builder
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IllegalArgumentException if {@code path} is invalid
   */
  public JsonCompareBuilder ignoreCase(String path) {
    PathValidator.validate(path);
    ignoreCasePaths.add(path);
    return this;
  }

  /**
   * Compares two JSON documents using the options configured on this builder.
   *
   * @param expected the expected JSON document
   * @param actual the actual JSON document
   * @return the comparison result
   * @throws NullPointerException if either argument is {@code null}
   */
  public ComparisonResult compare(String expected, String actual) {
    return build().compare(expected, actual);
  }

  /**
   * Compares two JSON files using the options configured on this builder.
   *
   * @param expected the path to the expected JSON file
   * @param actual the path to the actual JSON file
   * @return the comparison result
   * @throws NullPointerException if either path is {@code null}
   */
  public ComparisonResult compare(Path expected, Path actual) {
    return build().compare(expected, actual);
  }

  /**
   * Builds a reusable comparator using the current configuration.
   *
   * <p>The returned comparator is independent of subsequent changes made to this builder.
   *
   * @return a reusable configured JSON comparator
   */
  public JsonComparator build() {
    return new JsonComparator(
        new ComparisonOptions(
            ignoreArrayOrder,
            ignoredPaths,
            treatNullAndMissingAsEqual,
            numericTolerance,
            unorderedArrayPaths,
            pathNumericTolerances,
            nullAndMissingEqualPaths,
            ignoreCase,
            ignoreCasePaths));
  }
}
