package io.github.nigalranieri.jsondiffer.config;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ResultConfigTest {

  @Test
  void keepsAllDifferencesByDefault() {
    ComparisonResult original =
        JsonCompare.compare("{\"name\":\"Alice\",\"age\":30}", "{\"name\":\"Bob\"}");

    ComparisonResult filtered = new ResultConfig().apply(original);

    assertEquals(original.getDifferences(), filtered.getDifferences());
  }

  @Test
  void retainsOnlyConfiguredDifferenceTypes() {
    ComparisonResult original =
        JsonCompare.compare(
            "{\"name\":\"Alice\",\"status\":\"ACTIVE\",\"age\":30}",
            "{\"name\":\"Bob\",\"status\":\"active\"}");

    ResultConfig config = new ResultConfig();
    config.setTypes(Arrays.asList(DifferenceType.VALUE_MISMATCH, DifferenceType.MISSING_FIELD));

    ComparisonResult filtered = config.apply(original);

    assertEquals(2, filtered.getDifferences().size());
    assertEquals(DifferenceType.VALUE_MISMATCH, filtered.getDifferences().get(0).getType());
    assertEquals(DifferenceType.MISSING_FIELD, filtered.getDifferences().get(1).getType());
  }

  @Test
  void valueMismatchPatternDoesNotRemoveOtherDifferenceTypes() {
    ComparisonResult original =
        JsonCompare.compare(
            "{\"email\":\"alice@example.com\",\"name\":\"Alice\",\"age\":30}",
            "{\"email\":\"bob@example.com\",\"name\":\"Bob\"}");

    ResultConfig config = new ResultConfig();
    config.setValueMismatchPattern("^[^@]+@[^@]+$");

    ComparisonResult filtered = config.apply(original);

    assertEquals(2, filtered.getDifferences().size());

    assertTrue(
        filtered.getDifferences().stream()
            .anyMatch(
                difference ->
                    difference.getType() == DifferenceType.VALUE_MISMATCH
                        && difference.getPath().equals("$.email")));

    assertTrue(
        filtered.getDifferences().stream()
            .anyMatch(
                difference ->
                    difference.getType() == DifferenceType.MISSING_FIELD
                        && difference.getPath().equals("$.age")));
  }

  @Test
  void combinesTypeAndValueMismatchFiltering() {
    ComparisonResult original =
        JsonCompare.compare(
            "{\"email\":\"alice@example.com\",\"name\":\"Alice\",\"age\":30}",
            "{\"email\":\"bob@example.com\",\"name\":\"Bob\"}");

    ResultConfig config = new ResultConfig();
    config.setTypes(Arrays.asList(DifferenceType.VALUE_MISMATCH, DifferenceType.MISSING_FIELD));
    config.setValueMismatchPattern("^[^@]+@[^@]+$");

    ComparisonResult filtered = config.apply(original);

    assertEquals(2, filtered.getDifferences().size());
    assertEquals("$.email", filtered.getDifferences().get(0).getPath());
    assertEquals("$.age", filtered.getDifferences().get(1).getPath());
  }
}
