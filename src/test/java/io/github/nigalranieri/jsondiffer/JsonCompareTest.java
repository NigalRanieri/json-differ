package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

class JsonCompareTest {

  @Test
  void shouldConsiderIdenticalJsonEqual() {
    String first = "{\"name\":\"Alice\",\"age\":30}";
    String second = "{\"name\":\"Alice\",\"age\":30}";

    assertTrue(JsonCompare.equals(first, second));
  }

  @Test
  void shouldIgnoreObjectPropertyOrder() {
    String first = "{\"name\":\"Alice\",\"age\":30}";
    String second = "{\"age\":30,\"name\":\"Alice\"}";

    assertTrue(JsonCompare.equals(first, second));
  }

  @Test
  void shouldDetectDifferentValues() {
    String first = "{\"name\":\"Alice\",\"age\":30}";
    String second = "{\"name\":\"Alice\",\"age\":31}";

    assertFalse(JsonCompare.equals(first, second));
  }

  @Test
  void shouldConsiderArrayOrderSignificant() {
    String first = "{\"values\":[1,2,3]}";
    String second = "{\"values\":[3,2,1]}";

    assertFalse(JsonCompare.equals(first, second));
  }

  @Test
  void shouldRejectInvalidJson() {
    String invalid = "{\"name\":}";
    String valid = "{\"name\":\"Alice\"}";

    assertThrows(InvalidJsonException.class, () -> JsonCompare.equals(invalid, valid));
  }

  @Test
  void shouldReportDifferentValue() {
    String expected = "{\"name\":\"Alice\"}";
    String actual = "{\"name\":\"Bob\"}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals("Alice", difference.getExpected().getValue());
    assertEquals("Bob", difference.getActual().getValue());
  }

  @Test
  void shouldReportMissingField() {
    String expected = "{\"name\":\"Alice\",\"age\":30}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.age", difference.getPath());
    assertEquals(DifferenceType.MISSING_FIELD, difference.getType());

    assertEquals(30, difference.getExpected().getValue());
    assertTrue(difference.getActual().isMissing());
  }

  @Test
  void shouldReportUnexpectedField() {
    String expected = "{\"name\":\"Alice\"}";
    String actual = "{\"name\":\"Alice\",\"age\":30}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.age", difference.getPath());
    assertEquals(DifferenceType.UNEXPECTED_FIELD, difference.getType());

    assertTrue(difference.getExpected().isMissing());
    assertEquals(30, difference.getActual().getValue());
  }

  @Test
  void shouldDistinguishJsonNullFromMissingField() {
    String expected = "{\"value\":null}";
    String actual = "{}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    Difference difference = result.getDifferences().get(0);

    assertTrue(difference.getExpected().isNull());
    assertFalse(difference.getExpected().isMissing());

    assertTrue(difference.getActual().isMissing());
  }

  @Test
  void shouldReportDifferentArrayValue() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,4,3]}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.values[1]", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals(2, difference.getExpected().getValue());
    assertEquals(4, difference.getActual().getValue());
  }

  @Test
  void shouldReportMissingArrayElement() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,2]}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.values[2]", difference.getPath());
    assertEquals(DifferenceType.MISSING_ELEMENT, difference.getType());
    assertEquals(3, difference.getExpected().getValue());
    assertTrue(difference.getActual().isMissing());
  }

  @Test
  void shouldReportUnexpectedArrayElement() {
    String expected = "{\"values\":[1,2]}";
    String actual = "{\"values\":[1,2,3]}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.values[2]", difference.getPath());
    assertEquals(DifferenceType.UNEXPECTED_ELEMENT, difference.getType());
    assertTrue(difference.getExpected().isMissing());
    assertEquals(3, difference.getActual().getValue());
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
}
