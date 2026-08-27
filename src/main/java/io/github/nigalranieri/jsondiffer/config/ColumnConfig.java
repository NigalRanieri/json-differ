package io.github.nigalranieri.jsondiffer.config;

/**
 * Configuration for result table columns.
 *
 * <p>Column values that exceed the configured maximum width are wrapped across multiple table lines
 * rather than truncated.
 */
public final class ColumnConfig {

  private static final int DEFAULT_MAX_CELL_WIDTH = 40;

  private Integer maxCellWidth = DEFAULT_MAX_CELL_WIDTH;

  /**
   * Returns the maximum width used for formatted table cells.
   *
   * @return the maximum cell width
   */
  public Integer getMaxCellWidth() {
    return maxCellWidth;
  }

  /**
   * Configures the maximum width used for formatted table cells.
   *
   * <p>The value must be greater than zero. A {@code null} value restores the default width of
   * {@value #DEFAULT_MAX_CELL_WIDTH}.
   *
   * @param maxCellWidth the maximum cell width, or {@code null} to use the default
   * @throws IllegalArgumentException if {@code maxCellWidth} is not greater than zero
   */
  public void setMaxCellWidth(Integer maxCellWidth) {
    if (maxCellWidth == null) {
      this.maxCellWidth = DEFAULT_MAX_CELL_WIDTH;
      return;
    }

    if (maxCellWidth <= 0) {
      throw new IllegalArgumentException("Maximum cell width must be greater than zero");
    }

    this.maxCellWidth = maxCellWidth;
  }
}
