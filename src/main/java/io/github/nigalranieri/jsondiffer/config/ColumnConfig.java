package io.github.nigalranieri.jsondiffer.config;

public final class ColumnConfig {

  private static final int DEFAULT_MAX_CELL_WIDTH = 40;

  private Integer maxCellWidth = DEFAULT_MAX_CELL_WIDTH;

  public Integer getMaxCellWidth() {
    return maxCellWidth;
  }

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
