package io.github.nigalranieri.jsondiffer.internal;

import io.github.nigalranieri.jsondiffer.internal.path.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ComparisonOptions {

  private final boolean ignoreArrayOrder;
  private final Set<String> ignoredPaths;
  private final PathMatcher pathMatcher;
  private final boolean treatNullAndMissingAsEqual;
  private final Double numericTolerance;
  private final Set<String> unorderedArrayPaths;
  private final List<PathTolerance> pathNumericTolerances;

  public ComparisonOptions(
      boolean ignoreArrayOrder,
      Set<String> ignoredPaths,
      boolean treatNullAndMissingAsEqual,
      Double numericTolerance,
      Set<String> unorderedArrayPaths,
      List<PathTolerance> pathNumericTolerances) {

    this.ignoreArrayOrder = ignoreArrayOrder;
    this.ignoredPaths = Collections.unmodifiableSet(new HashSet<>(ignoredPaths));
    this.pathMatcher = new PathMatcher();
    this.treatNullAndMissingAsEqual = treatNullAndMissingAsEqual;
    this.numericTolerance = numericTolerance;
    this.unorderedArrayPaths = Collections.unmodifiableSet(new HashSet<>(unorderedArrayPaths));
    this.pathNumericTolerances =
        Collections.unmodifiableList(new ArrayList<>(pathNumericTolerances));
  }

  public boolean hasNumericTolerance() {
    return numericTolerance != null;
  }

  public double getNumericTolerance() {
    return numericTolerance;
  }

  public Double getNumericTolerance(String path) {
    Double resolvedTolerance = numericTolerance;

    for (PathTolerance pathTolerance : pathNumericTolerances) {
      if (pathMatcher.matches(pathTolerance.getPath(), path)) {
        resolvedTolerance = pathTolerance.getTolerance();
      }
    }

    return resolvedTolerance;
  }

  public boolean isTreatNullAndMissingAsEqual() {
    return treatNullAndMissingAsEqual;
  }

  public boolean isIgnoreArrayOrder() {
    return ignoreArrayOrder;
  }

  public boolean shouldIgnoreArrayOrder(String path) {
    if (ignoreArrayOrder) {
      return true;
    }

    for (String unorderedArrayPath : unorderedArrayPaths) {
      if (pathMatcher.matches(unorderedArrayPath, path)) {
        return true;
      }
    }

    return false;
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
