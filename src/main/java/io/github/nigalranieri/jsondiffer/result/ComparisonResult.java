package io.github.nigalranieri.jsondiffer.result;

import io.github.nigalranieri.jsondiffer.internal.format.TableFormatter;
import java.util.*;

/**
 * Immutable result of a JSON comparison.
 *
 * <p>A result is equal when no differences were detected. When differences are present, they are
 * retained in deterministic traversal order.
 *
 * <p>Results can be rendered in either traversal or grouped format through {@link
 * #format(ComparisonResultFormat)}. Calling {@link #toString()} uses {@link
 * ComparisonResultFormat#TRAVERSAL}.
 */
public final class ComparisonResult {

  private final List<Difference> differences;
  private static final int MAX_CELL_WIDTH = 40;

  /**
   * Creates a comparison result from the supplied differences.
   *
   * <p>The supplied list is defensively copied and the resulting collection is immutable.
   *
   * @param differences the detected differences, in traversal order
   * @throws NullPointerException if {@code differences} is {@code null} or contains a {@code null}
   *     element
   */
  public ComparisonResult(List<Difference> differences) {
    Objects.requireNonNull(differences, "differences");

    for (Difference difference : differences) {
      Objects.requireNonNull(difference, "difference");
    }

    this.differences = Collections.unmodifiableList(new ArrayList<>(differences));
  }

  /**
   * Indicates whether the compared JSON documents are equal according to the configured comparison
   * rules.
   *
   * @return {@code true} when no differences were detected; {@code false} otherwise
   */
  public boolean isEqual() {
    return differences.isEmpty();
  }

  /**
   * Returns the detected differences in traversal order.
   *
   * <p>The returned list is immutable.
   *
   * @return the detected differences
   */
  public List<Difference> getDifferences() {
    return differences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ComparisonResult)) {
      return false;
    }

    ComparisonResult that = (ComparisonResult) o;

    return Objects.equals(differences, that.differences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(differences);
  }

  /**
   * Formats this result using the requested presentation mode.
   *
   * <p>Traversal format preserves the original difference order and places the JSON path first.
   * Grouped format groups differences by type and places the difference type first.
   *
   * <p>Long cell values are wrapped across multiple table lines rather than truncated.
   *
   * @param format the desired result format
   * @return a human-readable representation of this comparison result
   * @throws NullPointerException if {@code format} is {@code null}
   */
  public String format(ComparisonResultFormat format) {
    Objects.requireNonNull(format, "format");

    if (isEqual()) {
      return "JSON is equal";
    }

    if (format == ComparisonResultFormat.GROUPED) {
      return formatGrouped();
    }

    return formatTraversal();
  }

  /**
   * Returns this result in traversal format.
   *
   * @return a human-readable traversal-order representation of this result
   */
  @Override
  public String toString() {
    return format(ComparisonResultFormat.TRAVERSAL);
  }

  private String formatTraversal() {
    List<String> headers = Arrays.asList("PATH", "TYPE", "EXPECTED", "ACTUAL");

    List<List<String>> rows = new ArrayList<>();

    for (Difference difference : differences) {
      rows.add(
          Arrays.asList(
              difference.getPath(),
              difference.getType().toString(),
              difference.getExpected().toString(),
              difference.getActual().toString()));
    }

    return formatSummary()
        + System.lineSeparator()
        + System.lineSeparator()
        + TableFormatter.format(headers, rows, MAX_CELL_WIDTH);
  }

  private String formatSummary() {
    return "JSON differs (" + differences.size() + " differences):";
  }

  private String formatGrouped() {
    Map<DifferenceType, List<Difference>> grouped = new LinkedHashMap<>();

    for (Difference difference : differences) {
      List<Difference> group = grouped.get(difference.getType());

      if (group == null) {
        group = new ArrayList<>();
        grouped.put(difference.getType(), group);
      }

      group.add(difference);
    }

    List<String> headers = Arrays.asList("TYPE", "PATH", "EXPECTED", "ACTUAL");

    List<List<String>> rows = new ArrayList<>();

    for (Map.Entry<DifferenceType, List<Difference>> entry : grouped.entrySet()) {
      for (Difference difference : entry.getValue()) {
        rows.add(
            Arrays.asList(
                difference.getType().toString(),
                difference.getPath(),
                difference.getExpected().toString(),
                difference.getActual().toString()));
      }
    }

    return formatSummary()
        + System.lineSeparator()
        + System.lineSeparator()
        + TableFormatter.format(headers, rows, MAX_CELL_WIDTH);
  }
}
