package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import org.junit.jupiter.api.Test;

class UnorderedArrayComparisonTest {

  @Test
  void shouldIgnoreArrayOrderWhenConfigured() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[3,1,2]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldRespectArrayOrderByDefault() {
    String expected = "{\"values\":[1,2,3]}";
    String actual = "{\"values\":[3,1,2]}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
  }

  @Test
  void shouldRespectDuplicateElementsWhenIgnoringArrayOrder() {
    String expected = "{\"values\":[1,1,2]}";
    String actual = "{\"values\":[1,2,2]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    assertFalse(result.isEqual());
  }

  @Test
  void shouldIgnoreObjectOrderInsideUnorderedArray() {
    String expected = "{\"users\":[{\"id\":1,\"name\":\"Alice\"},{\"id\":2,\"name\":\"Bob\"}]}";
    String actual = "{\"users\":[{\"id\":2,\"name\":\"Bob\"},{\"name\":\"Alice\",\"id\":1}]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldReportNestedDifferenceForSimilarObjectsInUnorderedArray() {
    String expected = "{\"users\":[{\"id\":1,\"name\":\"Alice\"}]}";
    String actual = "{\"users\":[{\"id\":1,\"name\":\"Alicia\"}]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.users[0].name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals("Alice", difference.getExpected().getValue());
    assertEquals("Alicia", difference.getActual().getValue());
  }

  @Test
  void shouldPreferExactMatchesInUnorderedArray() {
    String expected = "{\"users\":[{\"id\":1,\"name\":\"Alice\"},{\"id\":2,\"name\":\"Bob\"}]}";

    String actual = "{\"users\":[{\"id\":2,\"name\":\"Robert\"},{\"id\":1,\"name\":\"Alice\"}]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.users[1].name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals("Bob", difference.getExpected().getValue());
    assertEquals("Robert", difference.getActual().getValue());
  }
}
