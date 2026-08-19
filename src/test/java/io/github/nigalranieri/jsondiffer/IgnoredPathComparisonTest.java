package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

public class IgnoredPathComparisonTest {
  @Test
  void shouldIgnoreDifferenceAtConfiguredPath() {
    String expected = "{\"name\":\"Alice\",\"timestamp\":\"10:00\"}";
    String actual = "{\"name\":\"Alice\",\"timestamp\":\"15:00\"}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldStillReportDifferencesAtOtherPaths() {
    String expected = "{\"name\":\"Alice\",\"timestamp\":\"10:00\"}";
    String actual = "{\"name\":\"Bob\",\"timestamp\":\"15:00\"}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
  }

  @Test
  void shouldIgnoreEntireSubtree() {
    String expected =
        "{\"name\":\"Alice\",\"metadata\":{\"timestamp\":\"10:00\",\"requestId\":\"abc\"}}";

    String actual =
        "{\"name\":\"Alice\",\"metadata\":{\"timestamp\":\"15:00\",\"requestId\":\"xyz\"}}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.metadata").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreMissingFieldAtConfiguredPath() {
    String expected = "{\"name\":\"Alice\",\"timestamp\":\"10:00\"}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreUnexpectedFieldAtConfiguredPath() {
    String expected = "{\"name\":\"Alice\"}";
    String actual = "{\"name\":\"Alice\",\"timestamp\":\"10:00\"}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }
}
