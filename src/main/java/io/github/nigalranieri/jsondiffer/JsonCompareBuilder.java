package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.util.HashSet;
import java.util.Set;

public final class JsonCompareBuilder {

  private boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths = new HashSet<>();
  private boolean treatNullAndMissingAsEqual;
  private Double numericTolerance;

  JsonCompareBuilder() {}

  public JsonCompareBuilder ignorePath(String path) {
    ignoredPaths.add(path);
    return this;
  }

  public JsonCompareBuilder ignoreArrayOrder() {
    this.ignoreArrayOrder = true;
    return this;
  }

  public JsonCompareBuilder treatNullAndMissingAsEqual() {
    this.treatNullAndMissingAsEqual = true;
    return this;
  }

  public JsonCompareBuilder numericTolerance(double tolerance) {
    if (tolerance < 0) {
      throw new IllegalArgumentException("Numeric tolerance cannot be negative");
    }

    this.numericTolerance = tolerance;
    return this;
  }

  public ComparisonResult compare(String expected, String actual) {
    return build().compare(expected, actual);
  }

  public JsonComparator build() {
    return new JsonComparator(
        new ComparisonOptions(
            ignoreArrayOrder, ignoredPaths, treatNullAndMissingAsEqual, numericTolerance));
  }
}
