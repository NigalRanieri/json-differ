package io.github.nigalranieri.jsondiffer.config;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import io.github.nigalranieri.jsondiffer.result.DifferenceValueType;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class OutputConfigTest {

  @Test
  void formatsResultUsingDefaultOutputConfiguration() {
    ComparisonResult result =
        new ComparisonResult(
            Collections.singletonList(
                new Difference(
                    "$.name",
                    DifferenceType.VALUE_MISMATCH,
                    DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
                    DifferenceValue.of(DifferenceValueType.STRING, "Bob"))));

    OutputConfig config = new OutputConfig();

    assertEquals(result.format(ComparisonResultFormat.TRAVERSAL, 40), config.format(result));
  }

  @Test
  void formatsResultUsingConfiguredFormatAndMaximumCellWidth() {
    ComparisonResult result =
        new ComparisonResult(
            Collections.singletonList(
                new Difference(
                    "$.very.long.property.path",
                    DifferenceType.VALUE_MISMATCH,
                    DifferenceValue.of(DifferenceValueType.STRING, "a very long expected value"),
                    DifferenceValue.of(DifferenceValueType.STRING, "a very long actual value"))));

    OutputConfig config = new OutputConfig();
    config.setFormat(ComparisonResultFormat.GROUPED);

    ColumnConfig columns = new ColumnConfig();
    columns.setMaxCellWidth(10);
    config.setColumns(columns);

    assertEquals(result.format(ComparisonResultFormat.GROUPED, 10), config.format(result));
  }

  @Test
  void rejectsNullResult() {
    OutputConfig config = new OutputConfig();

    assertThrows(NullPointerException.class, () -> config.format(null));
  }

  @Test
  void acceptsNonNullFormat() {
    OutputConfig config = new OutputConfig();

    config.setFormat(ComparisonResultFormat.GROUPED);

    assertEquals(ComparisonResultFormat.GROUPED, config.getFormat());
  }

  @Test
  void acceptsNonNullColumnConfiguration() {
    OutputConfig config = new OutputConfig();
    ColumnConfig columns = new ColumnConfig();
    columns.setMaxCellWidth(80);

    config.setColumns(columns);

    assertSame(columns, config.getColumns());
  }
}
