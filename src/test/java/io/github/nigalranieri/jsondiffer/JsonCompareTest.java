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
    assertEquals("Alice", difference.getExpected());
    assertEquals("Bob", difference.getActual());
  }
}
