package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.internal.comparison.ComparisonEngine;
import io.github.nigalranieri.jsondiffer.internal.parser.JacksonJsonParser;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;

/**
 * Reusable JSON comparator created from a configured {@link JsonCompareBuilder}.
 *
 * <p>A comparator captures the builder configuration at the moment it is built and can be reused
 * across multiple comparisons.
 */
public final class JsonComparator {

  private final ComparisonOptions options;
  private final JacksonJsonParser parser;

  JsonComparator(ComparisonOptions options) {
    this.options = options;
    this.parser = new JacksonJsonParser();
  }

  /**
   * Compares two JSON documents using this comparator's configuration.
   *
   * @param expected the expected JSON document
   * @param actual the actual JSON document
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   */
  public ComparisonResult compare(String expected, String actual) {
    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }

  /**
   * Compares two JSON files using this comparator's configuration.
   *
   * @param expected the path to the expected JSON file
   * @param actual the path to the actual JSON file
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either path is {@code null}
   */
  public ComparisonResult compare(Path expected, Path actual) {
    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }
}
