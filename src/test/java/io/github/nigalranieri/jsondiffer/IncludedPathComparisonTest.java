package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

class IncludedPathComparisonTest {

  @Test
  void shouldReportDifferenceAtIncludedPath() {
    String expected = "{\"name\":\"Alice\",\"age\":30}";
    String actual = "{\"name\":\"Bob\",\"age\":30}";

    ComparisonResult result = JsonCompare.builder().includePath("$.name").compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());
    assertEquals("$.name", result.getDifferences().get(0).getPath());
  }

  @Test
  void shouldIgnoreDifferencesOutsideIncludedPath() {
    String expected = "{\"name\":\"Alice\",\"age\":30}";
    String actual = "{\"name\":\"Alice\",\"age\":40}";

    ComparisonResult result = JsonCompare.builder().includePath("$.name").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIncludeEntireSubtree() {
    String expected = "{\"user\":{\"name\":\"Alice\",\"age\":30},\"metadata\":{\"version\":1}}";

    String actual = "{\"user\":{\"name\":\"Bob\",\"age\":40},\"metadata\":{\"version\":2}}";

    ComparisonResult result = JsonCompare.builder().includePath("$.user").compare(expected, actual);

    assertEquals(2, result.getDifferences().size());

    assertTrue(
        result.getDifferences().stream()
            .map(Difference::getPath)
            .allMatch(path -> path.startsWith("$.user.")));
  }

  @Test
  void shouldIncludePathsUsingObjectWildcard() {
    String expected =
        "{\"users\":{\"primary\":{\"name\":\"Alice\"},\"secondary\":{\"name\":\"Bob\"}}}";
    String actual =
        "{\"users\":{\"primary\":{\"name\":\"Carol\"},\"secondary\":{\"name\":\"Dave\"}}}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.users.*.name").compare(expected, actual);

    assertEquals(2, result.getDifferences().size());
  }

  @Test
  void shouldIncludePathsUsingArrayWildcard() {
    String expected = "{\"users\":[{\"name\":\"Alice\",\"age\":30},{\"name\":\"Bob\",\"age\":40}]}";
    String actual = "{\"users\":[{\"name\":\"Carol\",\"age\":31},{\"name\":\"Dave\",\"age\":41}]}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.users[*].name").compare(expected, actual);

    assertEquals(2, result.getDifferences().size());
    assertTrue(
        result.getDifferences().stream()
            .map(Difference::getPath)
            .allMatch(path -> path.endsWith(".name")));
  }

  @Test
  void shouldIncludePathsUsingRecursiveWildcard() {
    String expected =
        "{\"user\":{\"email\":\"alice@example.com\","
            + "\"contact\":{\"email\":\"work@example.com\"}},"
            + "\"version\":1}";

    String actual =
        "{\"user\":{\"email\":\"bob@example.com\","
            + "\"contact\":{\"email\":\"office@example.com\"}},"
            + "\"version\":2}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.**.email").compare(expected, actual);

    assertEquals(2, result.getDifferences().size());
    assertTrue(
        result.getDifferences().stream()
            .map(Difference::getPath)
            .allMatch(path -> path.endsWith(".email")));
  }

  @Test
  void shouldIgnoreDifferencesInExcludedArrayElements() {
    String expected =
        "{\"users\":["
            + "{\"name\":\"Alice\",\"age\":30},"
            + "{\"name\":\"Bob\",\"age\":40}"
            + "]}";

    String actual =
        "{\"users\":["
            + "{\"name\":\"Carol\",\"age\":31},"
            + "{\"name\":\"Dave\",\"age\":41}"
            + "]}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.users[0].name").compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.users[0].name", result.getDifferences().get(0).getPath());
  }

  @Test
  void shouldIgnoreDifferencesInExcludedObjectBranches() {
    String expected =
        "{\"user\":{\"name\":\"Alice\",\"age\":30}," + "\"metadata\":{\"version\":1}}";

    String actual = "{\"user\":{\"name\":\"Bob\",\"age\":40}," + "\"metadata\":{\"version\":2}}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.user.name").compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.user.name", result.getDifferences().get(0).getPath());
  }

  @Test
  void shouldIgnoreMissingFieldOutsideIncludedPath() {
    String expected = "{\"user\":{\"name\":\"Alice\"},\"metadata\":{\"version\":1}}";

    String actual = "{\"user\":{\"name\":\"Alice\"},\"metadata\":{}}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.user.name").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldReportMissingFieldInsideIncludedPath() {
    String expected = "{\"user\":{\"name\":\"Alice\",\"age\":30}}";

    String actual = "{\"user\":{\"name\":\"Alice\"}}";

    ComparisonResult result = JsonCompare.builder().includePath("$.user").compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.user.age", result.getDifferences().get(0).getPath());
    assertEquals(DifferenceType.MISSING_FIELD, result.getDifferences().get(0).getType());
  }

  @Test
  void shouldIgnoreMissingArrayElementOutsideIncludedPath() {
    String expected = "{\"users\":[{\"name\":\"Alice\"},{\"name\":\"Bob\"}]}";

    String actual = "{\"users\":[{\"name\":\"Alice\"}]}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.users[0].name").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreMissingFieldOutsideIncludedPathWithRecursiveWildcard() {
    String expected = "{\"user\":{\"email\":\"alice@example.com\"},\"metadata\":{\"version\":1}}";

    String actual = "{\"user\":{\"email\":\"alice@example.com\"},\"metadata\":{}}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.**.email").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldReportMissingAncestorOfIncludedPath() {
    String expected = "{\"user\":{\"name\":\"Alice\"}}";

    String actual = "{}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.user.name").compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.user", result.getDifferences().get(0).getPath());
    assertEquals(DifferenceType.MISSING_FIELD, result.getDifferences().get(0).getType());
  }

  @Test
  void shouldReportMissingArrayElementContainingIncludedDescendant() {
    String expected = "{\"users\":[" + "{\"name\":\"Alice\"}," + "{\"name\":\"Bob\"}" + "]}";

    String actual = "{\"users\":[" + "{\"name\":\"Alice\"}" + "]}";

    ComparisonResult result =
        JsonCompare.builder().includePath("$.users[1].name").compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.users[1]", result.getDifferences().get(0).getPath());
    assertEquals(DifferenceType.MISSING_ELEMENT, result.getDifferences().get(0).getType());
  }

  @Test
  void ignoredPathShouldOverrideIncludedPath() {
    String expected = "{\"user\":{\"name\":\"Alice\",\"age\":30}}";

    String actual = "{\"user\":{\"name\":\"Bob\",\"age\":31}}";

    ComparisonResult result =
        JsonCompare.builder()
            .includePath("$.user")
            .ignorePath("$.user.name")
            .compare(expected, actual);

    assertEquals(1, result.getDifferences().size());
    assertEquals("$.user.age", result.getDifferences().get(0).getPath());
  }

  @Test
  void ignoredIncludedPathShouldProduceNoDifference() {
    String expected = "{\"user\":{\"name\":\"Alice\"}}";

    String actual = "{\"user\":{\"name\":\"Bob\"}}";

    ComparisonResult result =
        JsonCompare.builder()
            .includePath("$.user.name")
            .ignorePath("$.user.name")
            .compare(expected, actual);

    assertTrue(result.isEqual());
  }
}
