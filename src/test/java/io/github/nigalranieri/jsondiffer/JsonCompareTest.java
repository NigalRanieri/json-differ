package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.ResultConfig;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class JsonCompareTest {

  @Test
  void shouldConsiderIdenticalJsonEqual() {
    String first = "{\"name\":\"Alice\",\"age\":30}";
    String second = "{\"name\":\"Alice\",\"age\":30}";

    assertTrue(JsonCompare.areEqual(first, second));
  }

  @Test
  void shouldRejectInvalidJson() {
    String invalid = "{\"name\":}";
    String valid = "{\"name\":\"Alice\"}";

    assertThrows(InvalidJsonException.class, () -> JsonCompare.areEqual(invalid, valid));
  }

  @Test
  void shouldCompareUsingBuilder() {
    String expected = "{\"name\":\"Alice\"}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result = JsonCompare.builder().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldCreateReusableComparator() {
    JsonComparator comparator = JsonCompare.builder().build();

    ComparisonResult first = comparator.compare("{\"name\":\"Alice\"}", "{\"name\":\"Alice\"}");
    ComparisonResult second = comparator.compare("{\"age\":30}", "{\"age\":31}");

    assertTrue(first.isEqual());
    assertFalse(second.isEqual());
  }

  @Test
  void shouldRejectNullExpectedJson() {
    assertThrows(NullPointerException.class, () -> JsonCompare.compare((String) null, "{}"));
  }

  @Test
  void shouldRejectNullActualJson() {
    assertThrows(NullPointerException.class, () -> JsonCompare.compare("{}", (String) null));
  }

  @Test
  void shouldRejectWhitespaceOnlyJson() {
    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare("   \n\t", "{}"));
  }

  @Test
  void shouldRejectNaNNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().numericTolerance(Double.NaN));
  }

  @Test
  void shouldRejectInfiniteNumericTolerance() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonCompare.builder().numericTolerance(Double.POSITIVE_INFINITY));
  }

  @Test
  void shouldAllowZeroNumericTolerance() {
    ComparisonResult result =
        JsonCompare.builder().numericTolerance(0).compare("{\"value\":1}", "{\"value\":1}");

    assertTrue(result.isEqual());
  }

  @Test
  void shouldRejectNullIgnoredPath() {
    assertThrows(NullPointerException.class, () -> JsonCompare.builder().ignorePath(null));
  }

  @Test
  void shouldRejectBlankIgnoredPath() {
    assertThrows(IllegalArgumentException.class, () -> JsonCompare.builder().ignorePath("   "));
  }

  @Test
  void shouldRejectNullUnorderedArrayPath() {
    assertThrows(NullPointerException.class, () -> JsonCompare.builder().ignoreArrayOrder(null));
  }

  @Test
  void shouldRejectBlankUnorderedArrayPath() {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().ignoreArrayOrder("   "));
  }

  @Test
  void shouldRejectEmptyJsonString() {
    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare("", "{}"));
  }

  @Test
  void comparesFilesUsingConfiguration() throws IOException {
    Path expected = Files.createTempFile("json-differ-expected", ".json");
    Path actual = Files.createTempFile("json-differ-actual", ".json");

    try {
      Files.write(
          expected, "{\"name\":\"Alice\",\"status\":\"ACTIVE\"}".getBytes(StandardCharsets.UTF_8));

      Files.write(
          actual, "{\"name\":\"Bob\",\"status\":\"active\"}".getBytes(StandardCharsets.UTF_8));

      JsonDifferConfig config = new JsonDifferConfig();

      ResultConfig resultConfig = new ResultConfig();
      resultConfig.setTypes(Collections.singletonList(DifferenceType.CASE_MISMATCH));
      config.setResult(resultConfig);

      ComparisonResult result = JsonCompare.compare(expected, actual, config);

      assertEquals(1, result.getDifferences().size());
      assertEquals(DifferenceType.CASE_MISMATCH, result.getDifferences().get(0).getType());
    } finally {
      Files.deleteIfExists(expected);
      Files.deleteIfExists(actual);
    }
  }
}
