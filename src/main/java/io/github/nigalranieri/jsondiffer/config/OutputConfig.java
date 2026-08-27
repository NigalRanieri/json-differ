package io.github.nigalranieri.jsondiffer.config;

import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;

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
}
