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

  @Test
  void appliesNumericToleranceAtConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.price", 0.1)
            .compare("{\"price\":10.0,\"tax\":5.0}", "{\"price\":10.05,\"tax\":5.0}");

    assertTrue(result.isEqual());
  }

  @Test
  void doesNotApplyPathSpecificToleranceElsewhere() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.price", 0.1)
            .compare("{\"price\":10.0,\"tax\":5.0}", "{\"price\":10.05,\"tax\":5.05}");

    assertFalse(result.isEqual());
    assertEquals("$.tax", result.getDifferences().get(0).getPath());
  }

  @Test
  void pathSpecificToleranceOverridesGlobalTolerance() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance(0.01)
            .numericTolerance("$.price", 0.1)
            .compare("{\"price\":10.0,\"tax\":5.0}", "{\"price\":10.05,\"tax\":5.05}");

    assertFalse(result.isEqual());
    assertEquals("$.tax", result.getDifferences().get(0).getPath());
  }

  @Test
  void fallsBackToGlobalToleranceWhenPathDoesNotMatch() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance(0.1)
            .numericTolerance("$.price", 0.01)
            .compare("{\"price\":10.0,\"tax\":5.0}", "{\"price\":10.005,\"tax\":5.05}");

    assertTrue(result.isEqual());
  }

  @Test
  void appliesNumericToleranceWithObjectWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.measurements.*", 0.1)
            .compare(
                "{\"measurements\":{\"temperature\":20.0,\"pressure\":30.0}}",
                "{\"measurements\":{\"temperature\":20.05,\"pressure\":30.05}}");

    assertTrue(result.isEqual());
  }

  @Test
  void appliesNumericToleranceWithArrayWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.measurements[*].value", 0.1)
            .compare(
                "{\"measurements\":[{\"value\":10.0},{\"value\":20.0}]}",
                "{\"measurements\":[{\"value\":10.05},{\"value\":20.05}]}");

    assertTrue(result.isEqual());
  }

  @Test
  void appliesNumericToleranceWithRecursiveWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.**.price", 0.1)
            .compare(
                "{\"product\":{\"price\":10.0},\"nested\":{\"item\":{\"price\":20.0}}}",
                "{\"product\":{\"price\":10.05},\"nested\":{\"item\":{\"price\":20.05}}}");

    assertTrue(result.isEqual());
  }

  @Test
  void lastMatchingPathSpecificToleranceWins() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.**.price", 0.01)
            .numericTolerance("$.orders[*].price", 0.1)
            .compare("{\"orders\":[{\"price\":10.0}]}", "{\"orders\":[{\"price\":10.05}]}");

    assertTrue(result.isEqual());
  }

  @Test
  void globalNumericToleranceIsInclusiveAtProblematicDecimalBoundary() {
    ComparisonResult result =
        JsonCompare.builder().numericTolerance(0.1).compare("{\"price\":30.0}", "{\"price\":30.1}");

    assertTrue(result.isEqual());
  }

  @Test
  void pathSpecificNumericToleranceIsInclusiveAtProblematicDecimalBoundary() {
    ComparisonResult result =
        JsonCompare.builder()
            .numericTolerance("$.price", 0.1)
            .compare("{\"price\":30.0}", "{\"price\":30.1}");

    assertTrue(result.isEqual());
  }
}
