package io.github.nigalranieri.jsondiffer.config;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;
import java.util.Objects;

/**
 * Configuration for rendering comparison results.
 *
 * <p>Traversal formatting is used by default together with the default column configuration.
 */
public final class OutputConfig {

  private ComparisonResultFormat format = ComparisonResultFormat.TRAVERSAL;
  private ColumnConfig columns = new ColumnConfig();

  /**
   * Returns the configured result format.
   *
   * @return the result format
   */
  public ComparisonResultFormat getFormat() {
    return format;
  }

  /**
   * Configures the result format.
   *
   * <p>A {@code null} value is normalized to {@link ComparisonResultFormat#TRAVERSAL}.
   *
   * @param format the result format
   */
  public void setFormat(ComparisonResultFormat format) {
    this.format = format == null ? ComparisonResultFormat.TRAVERSAL : format;
  }

  /**
   * Returns the column configuration.
   *
   * @return the column configuration
   */
  public ColumnConfig getColumns() {
    return columns;
  }

  /**
   * Configures result table columns.
   *
   * <p>A {@code null} value is normalized to the default column configuration.
   *
   * @param columns the column configuration
   */
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
