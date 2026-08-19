package io.github.nigalranieri.jsondiffer.internal;

import io.github.nigalranieri.jsondiffer.internal.path.PathMatcher;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ComparisonOptions {

  private final boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths;
  private final PathMatcher pathMatcher;
  private final boolean treatNullAndMissingAsEqual;

  public ComparisonOptions(
      boolean ignoreArrayOrder, Set<String> ignoredPaths, boolean treatNullAndMissingAsEqual) {
    this.ignoreArrayOrder = ignoreArrayOrder;
    this.ignoredPaths = Collections.unmodifiableSet(new HashSet<>(ignoredPaths));
    this.pathMatcher = new PathMatcher();
    this.treatNullAndMissingAsEqual = treatNullAndMissingAsEqual;
  }

  public boolean isTreatNullAndMissingAsEqual() {
    return treatNullAndMissingAsEqual;
  }

  public boolean isIgnoreArrayOrder() {
    return ignoreArrayOrder;
  }

  public boolean isIgnoredPath(String path) {
    for (String ignoredPath : ignoredPaths) {
      if (pathMatcher.matches(ignoredPath, path)) {
        return true;
      }
    }

    return false;
  }
}
