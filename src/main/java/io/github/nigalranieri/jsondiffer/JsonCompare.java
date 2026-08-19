package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class JsonCompare {

  private JsonCompare() {}

  public static boolean equals(String first, String second) {
    return compare(first, second).isEqual();
  }

  public static ComparisonResult compare(String expected, String actual) {

    return builder().compare(expected, actual);
  }

  public static JsonCompareBuilder builder() {
    return new JsonCompareBuilder();
  }
}
