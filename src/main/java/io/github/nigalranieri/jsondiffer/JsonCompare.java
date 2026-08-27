package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.config.*;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Entry point for comparing JSON documents.
 *
 * <p>Comparisons are strict by default. Object property order is ignored, while array order is
 * significant. Additional comparison behavior can be configured through {@link #builder()}.
 *
 * <p>JSON can be provided either as raw string content or as {@link Path} instances pointing to
 * JSON files.
 */
public final class JsonCompare {

  private JsonCompare() {}

  /**
   * Determines whether two JSON documents are structurally equal using the default comparison
   * rules.
   *
   * @param first the first JSON document
   * @param second the second JSON document
   * @return {@code true} if the documents are equal; {@code false} otherwise
   * @throws NullPointerException if either argument is {@code null}
   */
  public static boolean equals(String first, String second) {
    return compare(first, second).isEqual();
  }

  /**
   * Compares two JSON documents using the default comparison rules.
   *
   * @param expected the expected JSON document
   * @param actual the actual JSON document
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   */
  public static ComparisonResult compare(String expected, String actual) {
    return builder().compare(expected, actual);
  }

  /**
   * Compares two JSON files using the default comparison rules.
   *
   * @param expected the path to the expected JSON file
   * @param actual the path to the actual JSON file
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either path is {@code null}
   */
  public static ComparisonResult compare(Path expected, Path actual) {
    return builder().compare(expected, actual);
  }

  /**
   * Creates a configured comparator from the supplied configuration.
   *
   * <p>The configuration is translated through the same builder API used for programmatic
   * configuration, preserving the same validation and comparison semantics.
   *
   * @param config the comparison configuration
   * @return a reusable configured comparator
   * @throws NullPointerException if {@code config} is {@code null}
   */
  public static JsonComparator fromConfig(JsonDifferConfig config) {
    Objects.requireNonNull(config, "config");

    JsonCompareBuilder builder = builder();

    ComparisonConfig comparison = config.getComparison();

    if (comparison == null) {
      return builder.build();
    }

    for (String path : comparison.getIgnorePaths()) {
      builder.ignorePath(path);
    }

    ArrayOrderConfig arrayOrder = comparison.getArrayOrder();

    if (arrayOrder != null) {
      if (arrayOrder.isIgnoreGlobally()) {
        builder.ignoreArrayOrder();
      }

      for (String path : arrayOrder.getIgnoreAt()) {
        builder.ignoreArrayOrder(path);
      }
    }

    NullAndMissingConfig nullAndMissing = comparison.getNullAndMissing();

    if (nullAndMissing != null) {
      if (nullAndMissing.isEqualGlobally()) {
        builder.treatNullAndMissingAsEqual();
      }

      for (String path : nullAndMissing.getEqualAt()) {
        builder.treatNullAndMissingAsEqual(path);
      }
    }

    NumericToleranceConfig numericTolerance = comparison.getNumericTolerance();

    if (numericTolerance != null) {
      if (numericTolerance.getGlobal() != null) {
        builder.numericTolerance(numericTolerance.getGlobal());
      }

      for (Map.Entry<String, Double> entry : numericTolerance.getPaths().entrySet()) {
        builder.numericTolerance(entry.getKey(), entry.getValue());
      }
    }

    IgnoreCaseConfig ignoreCase = comparison.getIgnoreCase();

    if (ignoreCase != null) {
      if (ignoreCase.isGlobally()) {
        builder.ignoreCase();
      }

      for (String path : ignoreCase.getPaths()) {
        builder.ignoreCase(path);
      }
    }

    return builder.build();
  }

  /**
   * Creates a builder for configuring JSON comparison behavior.
   *
   * @return a new comparison builder
   */
  public static JsonCompareBuilder builder() {
    return new JsonCompareBuilder();
  }
}
