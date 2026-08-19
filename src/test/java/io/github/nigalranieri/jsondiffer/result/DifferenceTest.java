package io.github.nigalranieri.jsondiffer.result;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DifferenceTest {

  @Test
  void shouldBeEqualWhenAllFieldsAreEqual() {
    Difference first =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference second =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldNotBeEqualWhenPathDiffers() {
    Difference first =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference second =
        new Difference(
            "$.user.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    assertNotEquals(first, second);
  }

  @Test
  void shouldRejectNullPath() {
    assertThrows(
        NullPointerException.class,
        () ->
            new Difference(
                null,
                DifferenceType.VALUE_MISMATCH,
                DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
                DifferenceValue.of(DifferenceValueType.STRING, "Bob")));
  }
}
