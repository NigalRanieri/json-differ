package io.github.nigalranieri.jsondiffer.config;

public final class ColumnConfig {

  private static final int DEFAULT_MAX_CELL_WIDTH = 40;

  private Integer maxCellWidth = DEFAULT_MAX_CELL_WIDTH;

  public Integer getMaxCellWidth() {
    return maxCellWidth;
  }

  public void setMaxCellWidth(Integer maxCellWidth) {
    this.maxCellWidth = maxCellWidth == null ? DEFAULT_MAX_CELL_WIDTH : maxCellWidth;
  }
}
