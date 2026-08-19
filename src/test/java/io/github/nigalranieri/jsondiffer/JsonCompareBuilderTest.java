package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonCompareBuilderTest {

  @ParameterizedTest
  @ValueSource(strings = {"users", "$.users[", "$.users[]", "$.users[abc]", "$.users..name"})
  void shouldRejectInvalidUnorderedArrayPath(String path) {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().ignoreArrayOrder(path));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "$",
        "$.user.name",
        "$.users[0]",
        "$.users[*]",
        "$.*.timestamp",
        "$.**.timestamp",
        "$.groups[*].users"
      })
  void shouldAcceptValidPaths(String path) {
    assertDoesNotThrow(() -> JsonCompare.builder().ignorePath(path));
  }

  @Test
  void shouldAllowDuplicateIgnoredPaths() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignorePath("$.timestamp")
            .ignorePath("$.timestamp")
            .compare("{\"timestamp\":\"10:00\"}", "{\"timestamp\":\"15:00\"}");

    assertTrue(result.isEqual());
  }

  @Test
  void shouldNotModifyBuiltComparatorWhenBuilderChanges() {
    JsonCompareBuilder builder = JsonCompare.builder().ignorePath("$.timestamp");

    JsonComparator first = builder.build();

    builder.ignorePath("$.requestId");

    JsonComparator second = builder.build();

    String expected = "{\"timestamp\":\"10:00\",\"requestId\":\"A\"}";
    String actual = "{\"timestamp\":\"15:00\",\"requestId\":\"B\"}";

    assertFalse(first.compare(expected, actual).isEqual());
    assertTrue(second.compare(expected, actual).isEqual());
  }

  @Test
  void shouldReuseComparatorAcrossComparisons() {
    JsonComparator comparator = JsonCompare.builder().ignorePath("$.timestamp").build();

    ComparisonResult first =
        comparator.compare(
            "{\"name\":\"Alice\",\"timestamp\":\"10:00\"}",
            "{\"name\":\"Alice\",\"timestamp\":\"15:00\"}");

    ComparisonResult second = comparator.compare("{\"name\":\"Alice\"}", "{\"name\":\"Bob\"}");

    assertTrue(first.isEqual());
    assertFalse(second.isEqual());
  }

  @ParameterizedTest
  @ValueSource(strings = {"null", "true", "false", "42", "42.5", "\"hello\"", "{}", "[]"})
  void shouldAcceptAnyValidJsonRoot(String json) {
    ComparisonResult result = JsonCompare.compare(json, json);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldAcceptWhitespaceAroundJson() {
    ComparisonResult result =
        JsonCompare.compare("  \n {\"name\":\"Alice\"} \t ", "{\"name\":\"Alice\"}");

    assertTrue(result.isEqual());
  }
}
