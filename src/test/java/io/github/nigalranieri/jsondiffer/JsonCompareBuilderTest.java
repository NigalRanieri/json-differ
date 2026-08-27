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

  @Test
  void shouldRemainStrictByDefault() {
    assertFalse(
        JsonCompare.compare(
                "{\"value\":1,\"name\":\"Alice\",\"optional\":null,\"items\":[1,2]}",
                "{\"value\":1.001,\"name\":\"alice\",\"items\":[2,1]}")
            .isEqual());
  }

  @Test
  void builtComparatorKeepsPathSpecificUnorderedArraySnapshot() {
    JsonCompareBuilder builder = JsonCompare.builder().ignoreArrayOrder("$.unordered");

    JsonComparator comparator = builder.build();

    builder.ignoreArrayOrder("$.other");

    ComparisonResult result = comparator.compare("{\"other\":[1,2]}", "{\"other\":[2,1]}");

    assertFalse(result.isEqual());
  }

  @Test
  void builtComparatorKeepsPathSpecificNumericToleranceSnapshot() {
    JsonCompareBuilder builder = JsonCompare.builder().numericTolerance("$.price", 0.1);

    JsonComparator comparator = builder.build();

    builder.numericTolerance("$.price", 10.0);

    ComparisonResult result = comparator.compare("{\"price\":10.0}", "{\"price\":10.5}");

    assertFalse(result.isEqual());
  }

  @Test
  void builtComparatorKeepsPathSpecificNullAndMissingSnapshot() {
    JsonCompareBuilder builder = JsonCompare.builder().treatNullAndMissingAsEqual("$.optional");

    JsonComparator comparator = builder.build();

    builder.treatNullAndMissingAsEqual("$.other");

    ComparisonResult result = comparator.compare("{\"other\":null}", "{}");

    assertFalse(result.isEqual());
  }

  @Test
  void builtComparatorKeepsPathSpecificIgnoreCaseSnapshot() {
    JsonCompareBuilder builder = JsonCompare.builder().ignoreCase("$.name");

    JsonComparator comparator = builder.build();

    builder.ignoreCase("$.city");

    ComparisonResult result = comparator.compare("{\"city\":\"Milan\"}", "{\"city\":\"milan\"}");

    assertFalse(result.isEqual());
  }

  @Test
  void builtComparatorRetainsConfiguredPathSpecificRules() {
    JsonComparator comparator =
        JsonCompare.builder()
            .numericTolerance("$.price", 0.1)
            .treatNullAndMissingAsEqual("$.optional")
            .ignoreCase("$.name")
            .build();

    ComparisonResult result =
        comparator.compare(
            "{\"price\":10.0,\"optional\":null,\"name\":\"Alice\"}",
            "{\"price\":10.05,\"name\":\"alice\"}");

    assertTrue(result.isEqual());
  }

  @Test
  void rejectsInvalidPathForNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().numericTolerance("price", 0.1));
  }

  @Test
  void rejectsInvalidPathForNullAndMissingEquivalence() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonCompare.builder().treatNullAndMissingAsEqual("optional"));
  }

  @Test
  void rejectsInvalidPathForIgnoreCase() {
    assertThrows(IllegalArgumentException.class, () -> JsonCompare.builder().ignoreCase("name"));
  }

  @Test
  void rejectsNullPathForNumericTolerance() {
    assertThrows(
        NullPointerException.class, () -> JsonCompare.builder().numericTolerance(null, 0.1));
  }

  @Test
  void rejectsNullPathForNullAndMissingEquivalence() {
    assertThrows(
        NullPointerException.class, () -> JsonCompare.builder().treatNullAndMissingAsEqual(null));
  }

  @Test
  void rejectsNullPathForIgnoreCase() {
    assertThrows(NullPointerException.class, () -> JsonCompare.builder().ignoreCase(null));
  }

  @Test
  void rejectsNegativePathSpecificNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonCompare.builder().numericTolerance("$.price", -0.1));
  }

  @Test
  void rejectsNaNPathSpecificNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonCompare.builder().numericTolerance("$.price", Double.NaN));
  }

  @Test
  void rejectsInfinitePathSpecificNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonCompare.builder().numericTolerance("$.price", Double.POSITIVE_INFINITY));
  }
}
