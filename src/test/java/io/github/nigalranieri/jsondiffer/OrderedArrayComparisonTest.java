package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.*;
import org.junit.jupiter.api.Test;

class OrderedArrayComparisonTest {

  @Test
  void shouldConsiderArrayOrderSignificant() {
    String first = "{\"values\":[1,2,3]}";
    String second = "{\"values\":[3,2,1]}";

    assertFalse(JsonCompare.equals(first, second));
  }

  @Test
  void shouldReportDifferentArrayValue() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[1,4,3]}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

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

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.values[2]", difference.getPath());
    assertEquals(DifferenceType.UNEXPECTED_ELEMENT, difference.getType());
    assertTrue(difference.getExpected().isMissing());
    assertEquals(3, difference.getActual().getValue());
  }
}
