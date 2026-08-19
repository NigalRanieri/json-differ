package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;

public final class JsonCompare {

  private JsonCompare() {}

  public static boolean equals(String first, String second) {
    return compare(first, second).isEqual();
  }

  public static ComparisonResult compare(String expected, String actual) {

    return builder().compare(expected, actual);
  }

  public static ComparisonResult compare(Path expected, Path actual) {
    return builder().compare(expected, actual);
  }

  public static JsonCompareBuilder builder() {
    return new JsonCompareBuilder();
  }
}
