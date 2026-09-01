package io.github.nigalranieri.jsondiffer.result;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.JsonCompare;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
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

  @Test
  void shouldFormatEqualResult() {
    ComparisonResult result = new ComparisonResult(Collections.<Difference>emptyList());

    assertEquals("JSON is equal", result.toString());
  }

  @Test
  void shouldFormatDifferencesInTraversalOrder() {
    Difference first =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference second =
        new Difference(
            "$.age",
            DifferenceType.MISSING_FIELD,
            DifferenceValue.of(DifferenceValueType.NUMBER, 30),
            DifferenceValue.missing());

    Difference third =
        new Difference(
            "$.active",
            DifferenceType.UNEXPECTED_FIELD,
            DifferenceValue.missing(),
            DifferenceValue.of(DifferenceValueType.BOOLEAN, true));

    ComparisonResult result = new ComparisonResult(Arrays.asList(first, second, third));

    String expected =
        "JSON differs (3 differences):"
            + System.lineSeparator()
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+"
            + System.lineSeparator()
            + "| PATH     | TYPE             | EXPECTED  | ACTUAL    |"
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+"
            + System.lineSeparator()
            + "| $.name   | VALUE_MISMATCH   | \"Alice\"   | \"Bob\"     |"
            + System.lineSeparator()
            + "| $.age    | MISSING_FIELD    | 30        | <missing> |"
            + System.lineSeparator()
            + "| $.active | UNEXPECTED_FIELD | <missing> | true      |"
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+";

    assertEquals(expected, result.toString());
  }

  @Test
  void shouldFormatDifferencesGroupedByType() {
    Difference first =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference second =
        new Difference(
            "$.age",
            DifferenceType.MISSING_FIELD,
            DifferenceValue.of(DifferenceValueType.NUMBER, 30),
            DifferenceValue.missing());

    Difference third =
        new Difference(
            "$.city",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Rome"),
            DifferenceValue.of(DifferenceValueType.STRING, "Milan"));

    Difference fourth =
        new Difference(
            "$.active",
            DifferenceType.UNEXPECTED_FIELD,
            DifferenceValue.missing(),
            DifferenceValue.of(DifferenceValueType.BOOLEAN, true));

    ComparisonResult result = new ComparisonResult(Arrays.asList(first, second, third, fourth));

    String expected =
        "JSON differs (4 differences):"
            + System.lineSeparator()
            + System.lineSeparator()
            + "+------------------+----------+-----------+-----------+"
            + System.lineSeparator()
            + "| TYPE             | PATH     | EXPECTED  | ACTUAL    |"
            + System.lineSeparator()
            + "+------------------+----------+-----------+-----------+"
            + System.lineSeparator()
            + "| VALUE_MISMATCH   | $.name   | \"Alice\"   | \"Bob\"     |"
            + System.lineSeparator()
            + "| VALUE_MISMATCH   | $.city   | \"Rome\"    | \"Milan\"   |"
            + System.lineSeparator()
            + "| MISSING_FIELD    | $.age    | 30        | <missing> |"
            + System.lineSeparator()
            + "| UNEXPECTED_FIELD | $.active | <missing> | true      |"
            + System.lineSeparator()
            + "+------------------+----------+-----------+-----------+";

    assertEquals(expected, result.format(ComparisonResultFormat.GROUPED));
  }

  @Test
  void shouldFormatEqualResultWhenGrouped() {
    ComparisonResult result = new ComparisonResult(Collections.<Difference>emptyList());

    assertEquals("JSON is equal", result.format(ComparisonResultFormat.GROUPED));
  }

  @Test
  void shouldUseTraversalFormatByDefault() {
    Difference difference =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(difference));

    assertEquals(result.format(ComparisonResultFormat.TRAVERSAL), result.toString());
  }

  @Test
  void shouldRejectNullDifference() {
    List<Difference> differences =
        Arrays.asList(
            new Difference(
                "$.name",
                DifferenceType.VALUE_MISMATCH,
                DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
                DifferenceValue.of(DifferenceValueType.STRING, "Bob")),
            null);

    assertThrows(NullPointerException.class, () -> new ComparisonResult(differences));
  }

  @Test
  void shouldRejectNullResultFormat() {
    ComparisonResult result = new ComparisonResult(Collections.<Difference>emptyList());

    assertThrows(NullPointerException.class, () -> result.format(null));
  }

  @Test
  void shouldPreserveTraversalOrderAcrossDifferentDifferenceTypes() {
    String expected = "{\"name\":\"Alice\",\"age\":30,\"city\":\"Rome\"}";

    String actual = "{\"name\":\"Bob\",\"city\":\"Rome\",\"active\":true}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertEquals(3, result.getDifferences().size());

    assertEquals(DifferenceType.VALUE_MISMATCH, result.getDifferences().get(0).getType());
    assertEquals("$.name", result.getDifferences().get(0).getPath());

    assertEquals(DifferenceType.MISSING_FIELD, result.getDifferences().get(1).getType());
    assertEquals("$.age", result.getDifferences().get(1).getPath());

    assertEquals(DifferenceType.UNEXPECTED_FIELD, result.getDifferences().get(2).getType());
    assertEquals("$.active", result.getDifferences().get(2).getPath());

    String expectedOutput =
        "JSON differs (3 differences):"
            + System.lineSeparator()
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+"
            + System.lineSeparator()
            + "| PATH     | TYPE             | EXPECTED  | ACTUAL    |"
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+"
            + System.lineSeparator()
            + "| $.name   | VALUE_MISMATCH   | \"Alice\"   | \"Bob\"     |"
            + System.lineSeparator()
            + "| $.age    | MISSING_FIELD    | 30        | <missing> |"
            + System.lineSeparator()
            + "| $.active | UNEXPECTED_FIELD | <missing> | true      |"
            + System.lineSeparator()
            + "+----------+------------------+-----------+-----------+";

    assertEquals(expectedOutput, result.toString());
  }

  @Test
  void shouldFormatTraversalWithCustomMaximumCellWidth() {
    Difference difference =
        new Difference(
            "$.very.long.property.path",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "a very long expected value"),
            DifferenceValue.of(DifferenceValueType.STRING, "a very long actual value"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(difference));

    String formatted = result.format(ComparisonResultFormat.TRAVERSAL, 10);

    assertTrue(formatted.contains("$.very.lon"));
    assertTrue(formatted.contains("g.property"));
  }

  @Test
  void existingFormatMethodShouldUseDefaultMaximumCellWidth() {
    Difference difference =
        new Difference(
            "$.path",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "expected"),
            DifferenceValue.of(DifferenceValueType.STRING, "actual"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(difference));

    assertEquals(
        result.format(ComparisonResultFormat.TRAVERSAL, 40),
        result.format(ComparisonResultFormat.TRAVERSAL));
  }

  @Test
  void shouldRejectNonPositiveMaximumCellWidth() {
    ComparisonResult result = new ComparisonResult(Collections.<Difference>emptyList());

    assertThrows(
        IllegalArgumentException.class, () -> result.format(ComparisonResultFormat.TRAVERSAL, 0));

    assertThrows(
        IllegalArgumentException.class, () -> result.format(ComparisonResultFormat.TRAVERSAL, -1));
  }

  @Test
  void shouldFilterDifferencesByType() {
    Difference valueMismatch =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference missingField =
        new Difference(
            "$.age",
            DifferenceType.MISSING_FIELD,
            DifferenceValue.of(DifferenceValueType.NUMBER, 30),
            DifferenceValue.missing());

    Difference secondValueMismatch =
        new Difference(
            "$.city",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Rome"),
            DifferenceValue.of(DifferenceValueType.STRING, "Milan"));

    ComparisonResult result =
        new ComparisonResult(Arrays.asList(valueMismatch, missingField, secondValueMismatch));

    ComparisonResult filtered = result.filter(DifferenceType.VALUE_MISMATCH);

    assertEquals(Arrays.asList(valueMismatch, secondValueMismatch), filtered.getDifferences());
  }

  @Test
  void shouldBeEqualWhenFilteringRemovesAllDifferences() {
    Difference difference =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(difference));

    ComparisonResult filtered = result.filter(DifferenceType.MISSING_FIELD);

    assertTrue(filtered.isEqual());
    assertFalse(result.isEqual());
  }

  @Test
  void shouldFilterDifferencesByMultipleTypes() {
    Difference valueMismatch =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference missingField =
        new Difference(
            "$.age",
            DifferenceType.MISSING_FIELD,
            DifferenceValue.of(DifferenceValueType.NUMBER, 30),
            DifferenceValue.missing());

    Difference caseMismatch =
        new Difference(
            "$.status",
            DifferenceType.CASE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "ACTIVE"),
            DifferenceValue.of(DifferenceValueType.STRING, "active"));

    ComparisonResult result =
        new ComparisonResult(Arrays.asList(valueMismatch, missingField, caseMismatch));

    ComparisonResult filtered =
        result.filter(DifferenceType.VALUE_MISMATCH, DifferenceType.CASE_MISMATCH);

    assertEquals(Arrays.asList(valueMismatch, caseMismatch), filtered.getDifferences());
  }

  @Test
  void shouldReturnEmptyResultWhenNoDifferenceTypesMatch() {
    Difference difference =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(difference));

    ComparisonResult filtered =
        result.filter(DifferenceType.MISSING_FIELD, DifferenceType.UNEXPECTED_FIELD);

    assertTrue(filtered.isEqual());
    assertTrue(filtered.getDifferences().isEmpty());
  }

  @Test
  void shouldNotModifyOriginalResultWhenFilteringByMultipleTypes() {
    Difference valueMismatch =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference missingField =
        new Difference(
            "$.age",
            DifferenceType.MISSING_FIELD,
            DifferenceValue.of(DifferenceValueType.NUMBER, 30),
            DifferenceValue.missing());

    ComparisonResult result = new ComparisonResult(Arrays.asList(valueMismatch, missingField));

    result.filter(DifferenceType.VALUE_MISMATCH);

    assertEquals(Arrays.asList(valueMismatch, missingField), result.getDifferences());
  }

  @Test
  void shouldFilterValueMismatchesByExpectedValuePattern() {
    Difference emailMismatch =
        new Difference(
            "$.email",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "alice@example.com"),
            DifferenceValue.of(DifferenceValueType.STRING, "invalid"));

    Difference nameMismatch =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    ComparisonResult result = new ComparisonResult(Arrays.asList(emailMismatch, nameMismatch));

    ComparisonResult filtered =
        result.filterValueMismatch(Pattern.compile("^[^@]+@[^@]+\\.[^@]+$"));

    assertEquals(Collections.singletonList(emailMismatch), filtered.getDifferences());
  }

  @Test
  void shouldFilterValueMismatchesByActualValuePattern() {
    Difference emailMismatch =
        new Difference(
            "$.email",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "invalid"),
            DifferenceValue.of(DifferenceValueType.STRING, "alice@example.com"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(emailMismatch));

    ComparisonResult filtered =
        result.filterValueMismatch(Pattern.compile("^[^@]+@[^@]+\\.[^@]+$"));

    assertEquals(Collections.singletonList(emailMismatch), filtered.getDifferences());
  }

  @Test
  void shouldIgnoreNonValueMismatchDifferencesWhenFilteringByPattern() {
    Difference caseMismatch =
        new Difference(
            "$.email",
            DifferenceType.CASE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "ALICE@EXAMPLE.COM"),
            DifferenceValue.of(DifferenceValueType.STRING, "alice@example.com"));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(caseMismatch));

    ComparisonResult filtered =
        result.filterValueMismatch(Pattern.compile("^[^@]+@[^@]+\\.[^@]+$"));

    assertTrue(filtered.getDifferences().isEmpty());
  }

  @Test
  void shouldIgnoreNonStringValueMismatchesWhenFilteringByPattern() {
    Difference numericMismatch =
        new Difference(
            "$.count",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.NUMBER, 10),
            DifferenceValue.of(DifferenceValueType.NUMBER, 20));

    ComparisonResult result = new ComparisonResult(Collections.singletonList(numericMismatch));

    ComparisonResult filtered = result.filterValueMismatch(Pattern.compile("\\d+"));

    assertTrue(filtered.getDifferences().isEmpty());
  }

  @Test
  void shouldPreserveOrderWhenFilteringValueMismatchesByPattern() {
    Difference first =
        new Difference(
            "$.primaryEmail",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "first@example.com"),
            DifferenceValue.of(DifferenceValueType.STRING, "invalid"));

    Difference ignored =
        new Difference(
            "$.name",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "Alice"),
            DifferenceValue.of(DifferenceValueType.STRING, "Bob"));

    Difference second =
        new Difference(
            "$.secondaryEmail",
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(DifferenceValueType.STRING, "invalid"),
            DifferenceValue.of(DifferenceValueType.STRING, "second@example.com"));

    ComparisonResult result = new ComparisonResult(Arrays.asList(first, ignored, second));

    ComparisonResult filtered =
        result.filterValueMismatch(Pattern.compile("^[^@]+@[^@]+\\.[^@]+$"));

    assertEquals(Arrays.asList(first, second), filtered.getDifferences());
  }

  @Test
  void shouldRejectNullPatternWhenFilteringValueMismatches() {
    ComparisonResult result = new ComparisonResult(Collections.emptyList());

    assertThrows(NullPointerException.class, () -> result.filterValueMismatch(null));
  }
}
