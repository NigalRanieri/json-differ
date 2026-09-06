package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.config.*;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.exception.JsonReadException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.io.IOException;
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
   * @throws InvalidJsonException if either document contains invalid JSON
   */
  public static boolean areEqual(String first, String second) {
    return compare(first, second).isEqual();
  }

  /**
   * Determines whether two JSON files are structurally equal using the default comparison rules.
   *
   * @param first the path to the first JSON file
   * @param second the path to the second JSON file
   * @return {@code true} if the files are equal; {@code false} otherwise
   * @throws NullPointerException if either path is {@code null}
   * @throws JsonReadException if either file cannot be read
   * @throws InvalidJsonException if either file contains invalid JSON
   */
  public static boolean areEqual(Path first, Path second) {
    return compare(first, second).isEqual();
  }

  /**
   * Determines whether a JSON document and a JSON file are structurally equal using the default
   * comparison rules.
   *
   * @param first the first JSON document
   * @param second the path to the second JSON file
   * @return {@code true} if the inputs are equal; {@code false} otherwise
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static boolean areEqual(String first, Path second) {
    return compare(first, second).isEqual();
  }

  /**
   * Determines whether a JSON file and a JSON document are structurally equal using the default
   * comparison rules.
   *
   * @param first the path to the first JSON file
   * @param second the second JSON document
   * @return {@code true} if the inputs are equal; {@code false} otherwise
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static boolean areEqual(Path first, String second) {
    return compare(first, second).isEqual();
  }

  /**
   * Compares two JSON documents using the default comparison rules.
   *
   * @param expected the expected JSON document
   * @param actual the actual JSON document
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   * @throws InvalidJsonException if either document contains invalid JSON
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
   * @throws JsonReadException if either file cannot be read
   * @throws InvalidJsonException if either file contains invalid JSON
   */
  public static ComparisonResult compare(Path expected, Path actual) {
    return builder().compare(expected, actual);
  }

  /**
   * Compares two JSON documents using the supplied configuration and applies the configured result
   * filtering.
   *
   * <p>Comparison settings determine how differences are detected, while result settings determine
   * which detected differences are retained. Output formatting is not applied by this method.
   *
   * @param expected the expected JSON document
   * @param actual the actual JSON document
   * @param config the configuration to apply
   * @return the filtered comparison result
   * @throws NullPointerException if {@code expected}, {@code actual}, or {@code config} is {@code
   *     null}
   * @throws InvalidJsonException if either document contains invalid JSON
   */
  public static ComparisonResult compare(String expected, String actual, JsonDifferConfig config) {

    Objects.requireNonNull(config, "config");

    ComparisonResult result = comparatorFromConfig(config).compare(expected, actual);

    return config.getResult().apply(result);
  }

  /**
   * Compares two JSON files using the supplied configuration and applies the configured result
   * filtering.
   *
   * <p>Comparison settings determine how differences are detected, while result settings determine
   * which detected differences are retained. Output formatting is not applied by this method.
   *
   * @param expected the path to the expected JSON file
   * @param actual the path to the actual JSON file
   * @param config the configuration to apply
   * @return the filtered comparison result
   * @throws NullPointerException if {@code expected}, {@code actual}, or {@code config} is {@code
   *     null}
   * @throws JsonReadException if either file cannot be read
   * @throws InvalidJsonException if either file contains invalid JSON
   */
  public static ComparisonResult compare(Path expected, Path actual, JsonDifferConfig config) {

    Objects.requireNonNull(config, "config");

    ComparisonResult result = comparatorFromConfig(config).compare(expected, actual);

    return config.getResult().apply(result);
  }

  /**
   * Compares a JSON document with a JSON file using the default comparison rules.
   *
   * @param expected the expected JSON document
   * @param actual the path to the actual JSON file
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the actual file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static ComparisonResult compare(String expected, Path actual) {
    return builder().compare(expected, actual);
  }

  /**
   * Compares a JSON file with a JSON document using the default comparison rules.
   *
   * @param expected the path to the expected JSON file
   * @param actual the actual JSON document
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the expected file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static ComparisonResult compare(Path expected, String actual) {
    return builder().compare(expected, actual);
  }

  /**
   * Compares a JSON document with a JSON file using the supplied configuration and applies the
   * configured result filtering.
   *
   * <p>Comparison settings determine how differences are detected, while result settings determine
   * which detected differences are retained. Output formatting is not applied by this method.
   *
   * @param expected the expected JSON document
   * @param actual the path to the actual JSON file
   * @param config the configuration to apply
   * @return the filtered comparison result
   * @throws NullPointerException if {@code expected}, {@code actual}, or {@code config} is {@code
   *     null}
   * @throws JsonReadException if the actual file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static ComparisonResult compare(String expected, Path actual, JsonDifferConfig config) {

    Objects.requireNonNull(config, "config");

    ComparisonResult result = comparatorFromConfig(config).compare(expected, actual);

    return config.getResult().apply(result);
  }

  /**
   * Compares a JSON file with a JSON document using the supplied configuration and applies the
   * configured result filtering.
   *
   * <p>Comparison settings determine how differences are detected, while result settings determine
   * which detected differences are retained. Output formatting is not applied by this method.
   *
   * @param expected the path to the expected JSON file
   * @param actual the actual JSON document
   * @param config the configuration to apply
   * @return the filtered comparison result
   * @throws NullPointerException if {@code expected}, {@code actual}, or {@code config} is {@code
   *     null}
   * @throws JsonReadException if the expected file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public static ComparisonResult compare(Path expected, String actual, JsonDifferConfig config) {

    Objects.requireNonNull(config, "config");

    ComparisonResult result = comparatorFromConfig(config).compare(expected, actual);

    return config.getResult().apply(result);
  }

  /**
   * Creates a reusable comparator from the comparison settings in the supplied configuration.
   *
   * <p>Only comparison settings are applied. Result filtering and output formatting are not part of
   * the returned comparator.
   *
   * @param config the configuration whose comparison settings should be used
   * @return a reusable comparator configured with the supplied comparison settings
   * @throws NullPointerException if {@code config} is {@code null}
   */
  public static JsonComparator comparatorFromConfig(JsonDifferConfig config) {
    Objects.requireNonNull(config, "config");

    JsonCompareBuilder builder = builder();

    ComparisonConfig comparison = config.getComparison();

    if (comparison == null) {
      return builder.build();
    }

    for (String path : comparison.getIgnorePaths()) {
      builder.ignorePath(path);
    }

    for (String path : comparison.getIncludePaths()) {
      builder.includePath(path);
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
   * Creates a reusable comparator from the comparison settings in the supplied configuration file.
   *
   * <p>The configuration format is selected by {@link JsonDifferConfigLoader#load(Path)}. Only
   * comparison settings are applied. Result filtering and output formatting are not part of the
   * returned comparator.
   *
   * @param configPath the path to the configuration file
   * @return a reusable comparator configured with the file's comparison settings
   * @throws NullPointerException if {@code configPath} is {@code null}
   * @throws IOException if the configuration file cannot be read
   */
  public static JsonComparator comparatorFromConfig(Path configPath) throws IOException {
    Objects.requireNonNull(configPath, "configPath");

    return comparatorFromConfig(JsonDifferConfigLoader.load(configPath));
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
