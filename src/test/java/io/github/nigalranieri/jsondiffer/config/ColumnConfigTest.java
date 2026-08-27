package io.github.nigalranieri.jsondiffer.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ColumnConfigTest {

  @Test
  void usesDefaultMaximumCellWidth() {
    ColumnConfig config = new ColumnConfig();

    assertEquals(Integer.valueOf(40), config.getMaxCellWidth());
  }

  @Test
  void acceptsPositiveMaximumCellWidth() {
    ColumnConfig config = new ColumnConfig();

    config.setMaxCellWidth(80);

    assertEquals(Integer.valueOf(80), config.getMaxCellWidth());
  }

  @Test
  void rejectsNonPositiveMaximumCellWidth() {
    ColumnConfig config = new ColumnConfig();

    assertThrows(IllegalArgumentException.class, () -> config.setMaxCellWidth(0));

    assertThrows(IllegalArgumentException.class, () -> config.setMaxCellWidth(-1));
  }

  @Test
  void resetsToDefaultWhenMaximumCellWidthIsNull() {
    ColumnConfig config = new ColumnConfig();
    config.setMaxCellWidth(80);

    config.setMaxCellWidth(null);

    assertEquals(Integer.valueOf(40), config.getMaxCellWidth());
  }
}
