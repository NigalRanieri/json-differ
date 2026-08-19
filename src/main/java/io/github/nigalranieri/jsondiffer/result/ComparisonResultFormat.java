package io.github.nigalranieri.jsondiffer.result;

/** Controls how a {@link ComparisonResult} is presented when formatted as text. */
public enum ComparisonResultFormat {

  /**
   * Preserves the original traversal order of detected differences.
   *
   * <p>The formatted table places the JSON path before the difference type.
   */
  TRAVERSAL,

  /**
   * Groups differences by {@link DifferenceType}.
   *
   * <p>The formatted table places the difference type before the JSON path while preserving
   * traversal order within each group.
   */
  GROUPED
}
