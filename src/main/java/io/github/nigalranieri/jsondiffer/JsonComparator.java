package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.exception.JsonReadException;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.internal.comparison.ComparisonEngine;
import io.github.nigalranieri.jsondiffer.internal.parser.JacksonJsonParser;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;

/**
 * Reusable JSON comparator containing a fixed set of comparison rules.
 *
 * <p>A comparator can be created from a {@link JsonCompareBuilder} or from the comparison settings
 * of a configuration. Result filtering and output formatting are not part of the comparator and
 * must be applied separately when needed.
 *
 * <p>Instances are immutable and can be reused for multiple comparisons.
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
   * @throws InvalidJsonException if either document contains invalid JSON
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
   * @throws JsonReadException if either file cannot be read
   * @throws InvalidJsonException if either file contains invalid JSON
   */
  public ComparisonResult compare(Path expected, Path actual) {
    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }

  /**
   * Compares a JSON document with a JSON file using this comparator's configuration.
   *
   * @param expected the expected JSON document
   * @param actual the path to the actual JSON file
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the actual file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public ComparisonResult compare(String expected, Path actual) {
    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }

  /**
   * Compares a JSON file with a JSON document using this comparator's configuration.
   *
   * @param expected the path to the expected JSON file
   * @param actual the actual JSON document
   * @return the comparison result containing any detected differences
   * @throws NullPointerException if either argument is {@code null}
   * @throws JsonReadException if the expected file cannot be read
   * @throws InvalidJsonException if either input contains invalid JSON
   */
  public ComparisonResult compare(Path expected, String actual) {
    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }
}
