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

  private static final String DEFAULT_EXPECTED_LABEL = "EXPECTED";
  private static final String DEFAULT_ACTUAL_LABEL = "ACTUAL";

  private String expectedLabel = DEFAULT_EXPECTED_LABEL;
  private String actualLabel = DEFAULT_ACTUAL_LABEL;

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

  /**
   * Returns the label used for the expected-value column.
   *
   * @return the expected-value column label
   */
  public String getExpectedLabel() {
    return expectedLabel;
  }

  /**
   * Configures the label used for the expected-value column.
   *
   * <p>A {@code null} value restores the default label {@code EXPECTED}.
   *
   * @param expectedLabel the expected-value column label, or {@code null} for the default
   */
  public void setExpectedLabel(String expectedLabel) {
    this.expectedLabel = expectedLabel == null ? DEFAULT_EXPECTED_LABEL : expectedLabel;
  }

  /**
   * Returns the label used for the actual-value column.
   *
   * @return the actual-value column label
   */
  public String getActualLabel() {
    return actualLabel;
  }

  /**
   * Configures the label used for the actual-value column.
   *
   * <p>A {@code null} value restores the default label {@code ACTUAL}.
   *
   * @param actualLabel the actual-value column label, or {@code null} for the default
   */
  public void setActualLabel(String actualLabel) {
    this.actualLabel = actualLabel == null ? DEFAULT_ACTUAL_LABEL : actualLabel;
  }
}
