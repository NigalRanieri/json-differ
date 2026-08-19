package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import org.junit.jupiter.api.Test;

class JsonCompareTest {

  @Test
  void shouldConsiderIdenticalJsonEqual() {
    String first = "{\"name\":\"Alice\",\"age\":30}";
    String second = "{\"name\":\"Alice\",\"age\":30}";

    assertTrue(JsonCompare.equals(first, second));
  }

  @Test
  void shouldRejectInvalidJson() {
    String invalid = "{\"name\":}";
    String valid = "{\"name\":\"Alice\"}";

    assertThrows(InvalidJsonException.class, () -> JsonCompare.equals(invalid, valid));
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
}
