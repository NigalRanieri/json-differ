package io.github.nigalranieri.jsondiffer.result;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ComparisonResultTest {

  @Test
  void shouldBeEqualWhenDifferencesAreEqual() {
    Difference difference =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    ComparisonResult first = new ComparisonResult(Collections.singletonList(difference));

    ComparisonResult second = new ComparisonResult(Collections.singletonList(difference));

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldNotChangeWhenOriginalListIsModified() {
    List<Difference> differences = new ArrayList<>();

    ComparisonResult result = new ComparisonResult(differences);

    differences.add(
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob")));

    assertTrue(result.isEqual());
    assertTrue(result.getDifferences().isEmpty());
  }

  @Test
  void shouldExposeImmutableDifferencesList() {
    ComparisonResult result = new ComparisonResult(Collections.<Difference>emptyList());

    assertThrows(UnsupportedOperationException.class, () -> result.getDifferences().add(null));
  }
}
