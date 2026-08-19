package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class JsonComparator {

  private final ComparisonOptions options;

  JsonComparator(ComparisonOptions options) {
    this.options = options;
  }

  public ComparisonResult compare(String expected, String actual) {
    return JsonCompare.compare(expected, actual, options);
  }
}
