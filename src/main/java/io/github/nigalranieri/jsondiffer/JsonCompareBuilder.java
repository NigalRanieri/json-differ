package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class JsonCompareBuilder {

  private boolean ignoreArrayOrder;

  JsonCompareBuilder() {}

  public JsonCompareBuilder ignoreArrayOrder() {
    this.ignoreArrayOrder = true;
    return this;
  }

  public ComparisonResult compare(String expected, String actual) {
    return build().compare(expected, actual);
  }

  public JsonComparator build() {
    return new JsonComparator(new ComparisonOptions(ignoreArrayOrder));
  }
}
