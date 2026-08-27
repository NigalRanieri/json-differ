package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.result.DifferenceValueType;
import io.github.nigalranieri.jsondiffer.support.JsonTestResource;
import java.util.List;
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
    String expected = JsonTestResource.load("json/unordered/users-similar-expected.json");

    String actual = JsonTestResource.load("json/unordered/users-similar-actual.json");

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.users[0].name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals("Alice", difference.getExpected().getValue());
    assertEquals("Alicia", difference.getActual().getValue());
  }

  @Test
  void shouldPreferExactMatchesInUnorderedArray() {
    String expected = JsonTestResource.load("json/unordered/users-exact-preferred-expected.json");

    String actual = JsonTestResource.load("json/unordered/users-exact-preferred-actual.json");

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    Difference difference = result.getDifferences().get(0);

    assertEquals("$.users[1].name", difference.getPath());
    assertEquals(DifferenceType.VALUE_MISMATCH, difference.getType());
    assertEquals("Bob", difference.getExpected().getValue());
    assertEquals("Robert", difference.getActual().getValue());
  }

  @Test
  void shouldIgnoreArrayOrderOnlyAtConfiguredPath() {
    String expected = "{\"users\":[1,2,3],\"scores\":[1,2,3]}";

    String actual = "{\"users\":[3,2,1],\"scores\":[3,2,1]}";

    ComparisonResult result =
        JsonCompare.builder().ignoreArrayOrder("$.users").compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(2, result.getDifferences().size());

    assertEquals("$.scores[0]", result.getDifferences().get(0).getPath());
    assertEquals("$.scores[2]", result.getDifferences().get(1).getPath());
  }

  @Test
  void shouldIgnoreArrayOrderAtConfiguredPath() {
    String expected = "{\"users\":[1,2,3],\"scores\":[1,2,3]}";

    String actual = "{\"users\":[3,2,1],\"scores\":[1,2,3]}";

    ComparisonResult result =
        JsonCompare.builder().ignoreArrayOrder("$.users").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreArrayOrderUsingPathWildcard() {
    String expected = "{\"groups\":[" + "{\"users\":[1,2,3]}," + "{\"users\":[4,5,6]}" + "]}";

    String actual = "{\"groups\":[" + "{\"users\":[3,2,1]}," + "{\"users\":[6,5,4]}" + "]}";

    ComparisonResult result =
        JsonCompare.builder().ignoreArrayOrder("$.groups[*].users").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldPreserveArrayDifferenceTypeWithoutExposingJackson() {
    ComparisonResult result = JsonCompare.compare("{\"value\":[1,2,3]}", "{}");

    Difference difference = result.getDifferences().get(0);

    assertEquals(DifferenceValueType.ARRAY, difference.getExpected().getType());
    assertTrue(difference.getExpected().getValue() instanceof List);
    assertFalse(difference.getExpected().getValue() instanceof JsonNode);
  }

  @Test
  void shouldIgnoreOrderInNestedArrays() {
    String expected = "{\"groups\":[[1,2,3],[4,5,6]]}";

    String actual = "{\"groups\":[[6,5,4],[3,2,1]]}";

    ComparisonResult result = JsonCompare.builder().ignoreArrayOrder().compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldIgnoreOrderOnlyForNestedArrayAtConfiguredPath() {
    String expected = "{\"groups\":[{\"values\":[1,2,3]}],\"scores\":[1,2,3]}";

    String actual = "{\"groups\":[{\"values\":[3,2,1]}],\"scores\":[3,2,1]}";

    ComparisonResult result =
        JsonCompare.builder().ignoreArrayOrder("$.groups[*].values").compare(expected, actual);

    assertFalse(result.isEqual());
    assertEquals(2, result.getDifferences().size());
    assertEquals("$.scores[0]", result.getDifferences().get(0).getPath());
    assertEquals("$.scores[2]", result.getDifferences().get(1).getPath());
  }

  @Test
  void unorderedArrayComparisonRespectsNumericTolerance() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.values")
            .numericTolerance("$.values[*]", 0.1)
            .compare("{\"values\":[10.0,20.0,30.0]}", "{\"values\":[30.05,10.05,20.05]}");

    assertTrue(result.isEqual());
  }

  @Test
  void unorderedArrayComparisonRespectsIgnoreCase() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.values")
            .ignoreCase("$.values[*]")
            .compare(
                "{\"values\":[\"Alice\",\"Bob\",\"Charlie\"]}",
                "{\"values\":[\"charlie\",\"alice\",\"BOB\"]}");

    assertTrue(result.isEqual());
  }

  @Test
  void numericToleranceInsideUnorderedArrayDoesNotApplyOutsideConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.values")
            .numericTolerance("$.values[*]", 0.1)
            .compare(
                "{\"values\":[10.0,20.0],\"other\":5.0}",
                "{\"values\":[20.05,10.05],\"other\":5.05}");

    assertFalse(result.isEqual());
    assertEquals("$.other", result.getDifferences().get(0).getPath());
  }

  @Test
  void ignoreCaseInsideUnorderedArrayDoesNotApplyOutsideConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.values")
            .ignoreCase("$.values[*]")
            .compare(
                "{\"values\":[\"Alice\",\"Bob\"],\"other\":\"Hello\"}",
                "{\"values\":[\"bob\",\"alice\"],\"other\":\"hello\"}");

    assertFalse(result.isEqual());
    assertEquals("$.other", result.getDifferences().get(0).getPath());
  }

  @Test
  void unorderedArrayComparisonRespectsNullAndMissingEquivalence() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.users")
            .treatNullAndMissingAsEqual("$.users[*].nickname")
            .compare(
                "{\"users\":[{\"id\":1,\"nickname\":null},{\"id\":2,\"nickname\":\"Bob\"}]}",
                "{\"users\":[{\"id\":2,\"nickname\":\"Bob\"},{\"id\":1}]}");

    assertTrue(result.isEqual());
  }

  @Test
  void nullAndMissingEquivalenceInsideUnorderedArrayDoesNotApplyOutsideConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.users")
            .treatNullAndMissingAsEqual("$.users[*].nickname")
            .compare(
                "{\"users\":[{\"id\":1,\"nickname\":null}],\"other\":null}",
                "{\"users\":[{\"id\":1}]}");

    assertFalse(result.isEqual());
    assertEquals("$.other", result.getDifferences().get(0).getPath());
  }

  @Test
  void unorderedArrayComparisonCanMatchObjectsUsingNullAndMissingEquivalence() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreArrayOrder("$.users")
            .treatNullAndMissingAsEqual("$.users[*].nickname")
            .compare(
                "{\"users\":["
                    + "{\"id\":1,\"nickname\":null},"
                    + "{\"id\":2,\"nickname\":\"Bob\"},"
                    + "{\"id\":3,\"nickname\":null}"
                    + "]}",
                "{\"users\":["
                    + "{\"id\":3},"
                    + "{\"id\":1},"
                    + "{\"id\":2,\"nickname\":\"Bob\"}"
                    + "]}");

    assertTrue(result.isEqual());
  }
}
