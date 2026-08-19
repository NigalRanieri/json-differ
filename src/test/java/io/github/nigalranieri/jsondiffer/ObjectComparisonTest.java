package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

class ObjectComparisonTest {

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
  void shouldTreatNullAndMissingAsDifferentByDefault() {
    String expected = "{\"name\":\"Alice\",\"age\":null}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());
    assertEquals(DifferenceType.MISSING_FIELD, result.getDifferences().get(0).getType());
  }

  @Test
  void shouldTreatNullAndMissingAsEqualWhenConfigured() {
    String expected = "{\"name\":\"Alice\",\"age\":null}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result =
        JsonCompare.builder().treatNullAndMissingAsEqual().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldTreatMissingAndNullAsEqualWhenConfigured() {
    String expected = "{\"name\":\"Alice\"}";
    String actual = "{\"name\":\"Alice\",\"age\":null}";

    ComparisonResult result =
        JsonCompare.builder().treatNullAndMissingAsEqual().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldStillReportMissingNonNullField() {
    String expected = "{\"name\":\"Alice\",\"age\":30}";
    String actual = "{\"name\":\"Alice\"}";

    ComparisonResult result =
        JsonCompare.builder().treatNullAndMissingAsEqual().compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(1, result.getDifferences().size());
    assertEquals(DifferenceType.MISSING_FIELD, result.getDifferences().get(0).getType());
  }

  @Test
  void shouldTreatNestedNullAndMissingAsEqualWhenConfigured() {
    String expected = "{\"user\":{\"name\":\"Alice\",\"metadata\":{\"timestamp\":null}}}";

    String actual = "{\"user\":{\"name\":\"Alice\",\"metadata\":{}}}";

    ComparisonResult result =
        JsonCompare.builder().treatNullAndMissingAsEqual().compare(expected, actual);

    assertTrue(result.isEqual());
  }
}
