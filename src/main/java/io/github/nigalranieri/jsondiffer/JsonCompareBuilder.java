package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.util.HashSet;
import java.util.Set;

public final class JsonCompareBuilder {

  private boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths = new HashSet<>();

  JsonCompareBuilder() {}

  public JsonCompareBuilder ignorePath(String path) {
    ignoredPaths.add(path);
    return this;
  }

  public JsonCompareBuilder ignoreArrayOrder() {
    this.ignoreArrayOrder = true;
    return this;
  }

  public ComparisonResult compare(String expected, String actual) {
    return build().compare(expected, actual);
  }

  public JsonComparator build() {
    return new JsonComparator(new ComparisonOptions(ignoreArrayOrder, ignoredPaths));
  }
}
