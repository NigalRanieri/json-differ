package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.internal.comparison.ComparisonEngine;
import io.github.nigalranieri.jsondiffer.internal.parser.JacksonJsonParser;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class JsonComparator {

  private final ComparisonOptions options;
  private final JacksonJsonParser parser;

  JsonComparator(ComparisonOptions options) {
    this.options = options;
    this.parser = new JacksonJsonParser();
  }

  public ComparisonResult compare(String expected, String actual) {

    JsonNode expectedNode = parser.parse(expected);
    JsonNode actualNode = parser.parse(actual);

    ComparisonEngine engine = new ComparisonEngine(options);

    return engine.compare(expectedNode, actualNode);
  }
}
