package io.github.nigalranieri.jsondiffer.internal.format;

import java.util.ArrayList;
import java.util.List;

public final class TableFormatter {

  private TableFormatter() {}

  public static String format(List<String> headers, List<List<String>> rows, int maxCellWidth) {

    int[] widths = calculateWidths(headers, rows, maxCellWidth);

    StringBuilder builder = new StringBuilder();

    appendBorder(builder, widths);
    appendRow(builder, headers, widths);
    appendBorder(builder, widths);

    for (List<String> row : rows) {
      appendRow(builder, row, widths);
    }

    appendBorder(builder, widths);

    return builder.toString();
  }

  private static int[] calculateWidths(
      List<String> headers, List<List<String>> rows, int maxCellWidth) {

    int[] widths = new int[headers.size()];

    for (int i = 0; i < headers.size(); i++) {
      widths[i] = Math.min(headers.get(i).length(), maxCellWidth);
    }

    for (List<String> row : rows) {
      for (int i = 0; i < row.size(); i++) {
        widths[i] = Math.min(maxCellWidth, Math.max(widths[i], row.get(i).length()));
      }
    }

    return widths;
  }

  private static void appendBorder(StringBuilder builder, int[] widths) {

    appendLineSeparatorIfNeeded(builder);

    for (int width : widths) {
      builder.append('+');

      for (int i = 0; i < width + 2; i++) {
        builder.append('-');
      }
    }

    builder.append('+');
  }

  private static void appendRow(StringBuilder builder, List<String> values, int[] widths) {

    List<List<String>> wrappedCells = new ArrayList<>();
    int rowHeight = 1;

    for (int i = 0; i < widths.length; i++) {
      List<String> wrapped = wrap(values.get(i), widths[i]);
      wrappedCells.add(wrapped);
      rowHeight = Math.max(rowHeight, wrapped.size());
    }

    for (int line = 0; line < rowHeight; line++) {
      appendLineSeparatorIfNeeded(builder);

      for (int column = 0; column < widths.length; column++) {
        List<String> wrapped = wrappedCells.get(column);

        String value = line < wrapped.size() ? wrapped.get(line) : "";

        builder.append("| ").append(value);

        appendSpaces(builder, widths[column] - value.length());

        builder.append(' ');
      }

      builder.append('|');
    }
  }

  private static void appendSpaces(StringBuilder builder, int count) {

    for (int i = 0; i < count; i++) {
      builder.append(' ');
    }
  }

  private static void appendLineSeparatorIfNeeded(StringBuilder builder) {

    if (builder.length() > 0) {
      builder.append(System.lineSeparator());
    }
  }

  private static List<String> wrap(String value, int width) {
    List<String> lines = new ArrayList<>();

    if (value.isEmpty()) {
      lines.add("");
      return lines;
    }

    for (int start = 0; start < value.length(); start += width) {
      int end = Math.min(start + width, value.length());
      lines.add(value.substring(start, end));
    }

    return lines;
  }
}
