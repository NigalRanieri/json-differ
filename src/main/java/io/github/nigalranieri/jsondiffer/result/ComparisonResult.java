package io.github.nigalranieri.jsondiffer.result;

import java.util.*;

public final class ComparisonResult {

  private final List<Difference> differences;

  public ComparisonResult(List<Difference> differences) {
    Objects.requireNonNull(differences, "differences");
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

  /*@Override
  public String toString() {
    if (isEqual()) {
      return "JSON is equal";
    }

    StringBuilder builder =
        new StringBuilder()
            .append("JSON differs (")
            .append(differences.size())
            .append(" differences):");

    for (Difference difference : differences) {
      builder.append(System.lineSeparator()).append("- ").append(difference);
    }

    return builder.toString();
  }*/

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
    StringBuilder builder =
        new StringBuilder()
            .append("JSON differs (")
            .append(differences.size())
            .append(" differences):");

    for (Difference difference : differences) {
      builder.append(System.lineSeparator()).append("- ").append(difference);
    }

    return builder.toString();
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

    StringBuilder builder =
        new StringBuilder()
            .append("JSON differs (")
            .append(differences.size())
            .append(" differences):");

    for (Map.Entry<DifferenceType, List<Difference>> entry : grouped.entrySet()) {
      builder
          .append(System.lineSeparator())
          .append(System.lineSeparator())
          .append(entry.getKey())
          .append(" (")
          .append(entry.getValue().size())
          .append("):");

      for (Difference difference : entry.getValue()) {
        builder
            .append(System.lineSeparator())
            .append("- ")
            .append(difference.getPath())
            .append(": expected=")
            .append(difference.getExpected())
            .append(", actual=")
            .append(difference.getActual());
      }
    }

    return builder.toString();
  }
}
