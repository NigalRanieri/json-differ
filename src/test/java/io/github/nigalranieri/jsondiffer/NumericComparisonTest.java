package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

class NumericComparisonTest {
  @Test
  void shouldTreatNumbersWithinToleranceAsEqual() {
    String expected = "{\"value\":10.0}";
    String actual = "{\"value\":10.0005}";

    ComparisonResult result =
        JsonCompare.builder().numericTolerance(0.001).compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldReportNumbersOutsideTolerance() {
    String expected = "{\"value\":10.0}";
    String actual = "{\"value\":10.01}";

    ComparisonResult result =
        JsonCompare.builder().numericTolerance(0.001).compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());
    assertEquals("$.value", result.getDifferences().get(0).getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, result.getDifferences().get(0).getType());
  }

  @Test
  void shouldCompareNumbersExactlyByDefault() {
    String expected = "{\"value\":10.0}";
    String actual = "{\"value\":10.0005}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
  }

  @Test
  void shouldRejectNegativeNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().numericTolerance(-0.1));
  }

  @Test
  void shouldTreatNumbersExactlyAtToleranceAsEqual() {
    String expected = "{\"value\":10.0}";
    String actual = "{\"value\":10.001}";

    ComparisonResult result =
        JsonCompare.builder().numericTolerance(0.001).compare(expected, actual);

    assertTrue(result.isEqual());
  }
}
