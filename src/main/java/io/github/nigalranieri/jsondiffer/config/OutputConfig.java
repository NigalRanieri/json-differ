package io.github.nigalranieri.jsondiffer.config;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;
import java.util.Objects;

public final class OutputConfig {

  private ComparisonResultFormat format = ComparisonResultFormat.TRAVERSAL;
  private ColumnConfig columns = new ColumnConfig();

  public ComparisonResultFormat getFormat() {
    return format;
  }

  public ColumnConfig getColumns() {
    return columns;
  }

  public void setFormat(ComparisonResultFormat format) {
    this.format = format == null ? ComparisonResultFormat.TRAVERSAL : format;
  }

  public void setColumns(ColumnConfig columns) {
    this.columns = columns == null ? new ColumnConfig() : columns;
  }

  /**
   * Formats the supplied comparison result using this output configuration.
   *
   * @param result the comparison result to format
   * @return the formatted comparison result
   * @throws NullPointerException if {@code result} is {@code null}
   */
  public String format(ComparisonResult result) {
    Objects.requireNonNull(result, "result");

    return result.format(format, columns.getMaxCellWidth());
  }
}
