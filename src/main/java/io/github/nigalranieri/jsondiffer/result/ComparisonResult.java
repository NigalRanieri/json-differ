package io.github.nigalranieri.jsondiffer.result;

import io.github.nigalranieri.jsondiffer.internal.format.TableFormatter;
import java.util.*;

public final class ComparisonResult {

  private final List<Difference> differences;
  private static final int MAX_CELL_WIDTH = 40;

  public ComparisonResult(List<Difference> differences) {
    Objects.requireNonNull(differences, "differences");

    for (Difference difference : differences) {
      Objects.requireNonNull(difference, "difference");
    }

    this.differences = Collections.unmodifiableList(new ArrayList<>(differences));
  }

  public boolean isEqual() {
    return differences.isEmpty();
  }

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
