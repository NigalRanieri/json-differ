package io.github.nigalranieri.jsondiffer.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ComparisonOptions {

  private final boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths;

  public ComparisonOptions(boolean ignoreArrayOrder, Set<String> ignoredPaths) {
    this.ignoreArrayOrder = ignoreArrayOrder;
    this.ignoredPaths = Collections.unmodifiableSet(new HashSet<>(ignoredPaths));
  }

  public boolean isIgnoreArrayOrder() {
    return ignoreArrayOrder;
  }

  public boolean isIgnoredPath(String path) {
    return ignoredPaths.contains(path);
  }
}
