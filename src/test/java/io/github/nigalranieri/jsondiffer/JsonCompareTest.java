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
}
