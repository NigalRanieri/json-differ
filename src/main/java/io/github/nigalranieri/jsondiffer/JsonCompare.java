package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;

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
   * Creates a builder for configuring JSON comparison behavior.
   *
   * @return a new comparison builder
   */
  public static JsonCompareBuilder builder() {
    return new JsonCompareBuilder();
  }
}
