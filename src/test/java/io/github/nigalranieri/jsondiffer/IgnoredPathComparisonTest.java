package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.support.JsonTestResource;
import org.junit.jupiter.api.Test;

class IgnoredPathComparisonTest {
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

  @Test
  void shouldIgnoreArrayElementsUsingWildcard() {
    String expected =
        "{\"users\":["
            + "{\"name\":\"Alice\",\"timestamp\":\"10:00\"},"
            + "{\"name\":\"Bob\",\"timestamp\":\"11:00\"}"
            + "]}";

    String actual =
        "{\"users\":["
            + "{\"name\":\"Alice\",\"timestamp\":\"15:00\"},"
            + "{\"name\":\"Bob\",\"timestamp\":\"16:00\"}"
            + "]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.users[*].timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldNotIgnoreNonMatchingWildcardPath() {
    String expected = "{\"users\":[{\"name\":\"Alice\",\"timestamp\":\"10:00\"}]}";

    String actual = "{\"users\":[{\"name\":\"Bob\",\"timestamp\":\"15:00\"}]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.users[*].timestamp").compare(expected, actual);

    assertFalse(result.isEqual());

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.users[0].name", result.getDifferences().get(0).getPath());
  }

  @Test
  void shouldIgnorePathAtAnyDepthUsingRecursiveWildcard() {
    String expected =
        "{\"timestamp\":\"10:00\","
            + "\"metadata\":{\"timestamp\":\"11:00\"},"
            + "\"users\":[{\"timestamp\":\"12:00\"}]}";

    String actual =
        "{\"timestamp\":\"20:00\","
            + "\"metadata\":{\"timestamp\":\"21:00\"},"
            + "\"users\":[{\"timestamp\":\"22:00\"}]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.**.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreExactArrayIndex() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,9,3]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.values[1]").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreAllArrayElementsUsingWildcard() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[9,8,7]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.values[*]").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreMissingArrayElementAtConfiguredPath() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,2]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.values[2]").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreUnexpectedArrayElementAtConfiguredPath() {
    String expected = "{\"values\":[1,2]}";
    String actual = "{\"values\":[1,2,3]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.values[2]").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreMissingArrayElementUsingWildcard() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,2]}";

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.values[*]").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreUnmatchedElementsUsingWildcardInUnorderedArray() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[9]}";

    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder()
            .ignorePath("$.values[*]")
            .compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreRecursiveWildcardThroughArrays() {
    String expected = JsonTestResource.load("json/ignored-path/recursive-array-expected.json");

    String actual = JsonTestResource.load("json/ignored-path/recursive-array-actual.json");

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.**.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }
}
