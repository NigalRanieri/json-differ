package io.github.nigalranieri.jsondiffer.internal;

public final class ComparisonOptions {

  private final boolean ignoreArrayOrder;

  public ComparisonOptions(boolean ignoreArrayOrder) {
    this.ignoreArrayOrder = ignoreArrayOrder;
  }

  public boolean isIgnoreArrayOrder() {
    return ignoreArrayOrder;
  }
}
