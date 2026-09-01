package io.github.nigalranieri.jsondiffer.internal.comparison;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.*;
import java.math.BigDecimal;
import java.util.*;

public final class ComparisonEngine {

  private final ComparisonOptions options;

  public ComparisonEngine(ComparisonOptions options) {
    this.options = options;
  }

  public ComparisonResult compare(JsonNode expected, JsonNode actual) {

    List<Difference> differences = new ArrayList<>();

    compareNodes("$", expected, actual, differences);

    return new ComparisonResult(differences);
  }

  private void compareNodes(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    if (options.isIgnoredPath(path)) {
      return;
    }

    if (expected.equals(actual)) {
      return;
    }

    if (expected.isNumber() && actual.isNumber()) {
      Double tolerance = options.getNumericTolerance(path);

      if (tolerance != null) {
        BigDecimal expectedValue = expected.decimalValue();
        BigDecimal actualValue = actual.decimalValue();
        BigDecimal difference = expectedValue.subtract(actualValue).abs();
        BigDecimal allowedTolerance = BigDecimal.valueOf(tolerance);

        if (difference.compareTo(allowedTolerance) <= 0) {
          return;
        }
      }
    }

    if (expected.isObject() && actual.isObject()) {
      compareObjects(path, expected, actual, differences);
      return;
    }

    if (expected.isArray() && actual.isArray()) {
      compareArrays(path, expected, actual, differences);
      return;
    }

    if (expected.isTextual() && actual.isTextual()) {
      boolean differsOnlyByCase = expected.textValue().equalsIgnoreCase(actual.textValue());

      if (differsOnlyByCase) {
        if (options.shouldIgnoreCase(path)) {
          return;
        }

        differences.add(
            new Difference(
                path,
                DifferenceType.CASE_MISMATCH,
                toDifferenceValue(expected),
                toDifferenceValue(actual)));
        return;
      }
    }

    differences.add(
        new Difference(
            path,
            DifferenceType.VALUE_MISMATCH,
            toDifferenceValue(expected),
            toDifferenceValue(actual)));
  }

  private static DifferenceValue toDifferenceValue(JsonNode node) {
    if (node.isNull()) {
      return DifferenceValue.ofNull();
    }

    if (node.isTextual()) {
      return DifferenceValue.of(DifferenceValueType.STRING, node.textValue());
    }

    if (node.isNumber()) {
      return DifferenceValue.of(DifferenceValueType.NUMBER, node.numberValue());
    }

    if (node.isBoolean()) {
      return DifferenceValue.of(DifferenceValueType.BOOLEAN, node.booleanValue());
    }

    if (node.isObject()) {
      return DifferenceValue.of(DifferenceValueType.OBJECT, toJavaValue(node));
    }

    if (node.isArray()) {
      return DifferenceValue.of(DifferenceValueType.ARRAY, toJavaValue(node));
    }

    throw new IllegalArgumentException("Unsupported JSON node type: " + node.getNodeType());
  }

  private static Object toJavaValue(JsonNode node) {
    if (node.isNull()) {
      return null;
    }

    if (node.isTextual()) {
      return node.textValue();
    }

    if (node.isNumber()) {
      return node.numberValue();
    }

    if (node.isBoolean()) {
      return node.booleanValue();
    }

    if (node.isObject()) {
      Map<String, Object> object = new LinkedHashMap<>();

      node.fields()
          .forEachRemaining(field -> object.put(field.getKey(), toJavaValue(field.getValue())));

      return object;
    }

    if (node.isArray()) {
      List<Object> array = new ArrayList<>();

      node.elements().forEachRemaining(element -> array.add(toJavaValue(element)));

      return array;
    }

    throw new IllegalArgumentException("Unsupported JSON node type: " + node.getNodeType());
  }

  private void compareObjects(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    Iterator<Map.Entry<String, JsonNode>> expectedFields = expected.fields();

    while (expectedFields.hasNext()) {
      Map.Entry<String, JsonNode> field = expectedFields.next();
      String fieldName = field.getKey();
      String fieldPath = path + "." + fieldName;

      if (options.isIgnoredPath(fieldPath)) {
        continue;
      }

      if (!actual.has(fieldName)) {
        if (options.shouldTreatNullAndMissingAsEqual(fieldPath) && field.getValue().isNull()) {
          continue;
        }

        differences.add(
            new Difference(
                fieldPath,
                DifferenceType.MISSING_FIELD,
                toDifferenceValue(field.getValue()),
                DifferenceValue.missing()));
        continue;
      }

      compareNodes(fieldPath, field.getValue(), actual.get(fieldName), differences);
    }

    Iterator<Map.Entry<String, JsonNode>> actualFields = actual.fields();

    while (actualFields.hasNext()) {
      Map.Entry<String, JsonNode> field = actualFields.next();
      String fieldName = field.getKey();
      String fieldPath = path + "." + fieldName;

      if (options.isIgnoredPath(fieldPath)) {
        continue;
      }

      if (!expected.has(fieldName)) {
        if (options.shouldTreatNullAndMissingAsEqual(fieldPath) && field.getValue().isNull()) {
          continue;
        }

        differences.add(
            new Difference(
                fieldPath,
                DifferenceType.UNEXPECTED_FIELD,
                DifferenceValue.missing(),
                toDifferenceValue(field.getValue())));
      }
    }
  }

  private void compareArrays(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    if (options.shouldIgnoreArrayOrder(path)) {
      compareUnorderedArrays(path, expected, actual, differences);
      return;
    }

    int commonSize = Math.min(expected.size(), actual.size());

    for (int i = 0; i < commonSize; i++) {
      compareNodes(path + "[" + i + "]", expected.get(i), actual.get(i), differences);
    }

    for (int i = commonSize; i < expected.size(); i++) {
      String elementPath = path + "[" + i + "]";

      if (options.isIgnoredPath(elementPath)) {
        continue;
      }

      differences.add(
          new Difference(
              elementPath,
              DifferenceType.MISSING_ELEMENT,
              toDifferenceValue(expected.get(i)),
              DifferenceValue.missing()));
    }

    for (int i = commonSize; i < actual.size(); i++) {
      String elementPath = path + "[" + i + "]";

      if (options.isIgnoredPath(elementPath)) {
        continue;
      }

      differences.add(
          new Difference(
              elementPath,
              DifferenceType.UNEXPECTED_ELEMENT,
              DifferenceValue.missing(),
              toDifferenceValue(actual.get(i))));
    }
  }

  private void compareUnorderedArrays(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    boolean[] expectedMatched = new boolean[expected.size()];
    boolean[] actualMatched = new boolean[actual.size()];

    matchExactElements(path, expected, actual, expectedMatched, actualMatched);

    matchSimilarElements(path, expected, actual, expectedMatched, actualMatched, differences);

    addUnmatchedElements(path, expected, actual, expectedMatched, actualMatched, differences);
  }

  private boolean nodesAreEqual(String path, JsonNode expected, JsonNode actual) {
    List<Difference> differences = new ArrayList<>();
    compareNodes(path, expected, actual, differences);
    return differences.isEmpty();
  }

  private void matchExactElements(
      String path,
      JsonNode expected,
      JsonNode actual,
      boolean[] expectedMatched,
      boolean[] actualMatched) {

    for (int i = 0; i < expected.size(); i++) {
      String elementPath = path + "[" + i + "]";

      for (int j = 0; j < actual.size(); j++) {
        if (actualMatched[j]) {
          continue;
        }

        if (nodesAreEqual(elementPath, expected.get(i), actual.get(j))) {
          expectedMatched[i] = true;
          actualMatched[j] = true;
          break;
        }
      }
    }
  }

  private void matchSimilarElements(
      String path,
      JsonNode expected,
      JsonNode actual,
      boolean[] expectedMatched,
      boolean[] actualMatched,
      List<Difference> differences) {

    for (int i = 0; i < expected.size(); i++) {

      if (expectedMatched[i]) {
        continue;
      }

      MatchCandidate bestMatch =
          findBestMatch(path + "[" + i + "]", expected.get(i), actual, actualMatched);

      if (bestMatch == null) {
        continue;
      }

      expectedMatched[i] = true;
      actualMatched[bestMatch.getActualIndex()] = true;

      differences.addAll(bestMatch.getDifferences());
    }
  }

  private MatchCandidate findBestMatch(
      String path, JsonNode expected, JsonNode actual, boolean[] actualMatched) {

    MatchCandidate bestMatch = null;

    for (int j = 0; j < actual.size(); j++) {

      if (actualMatched[j]) {
        continue;
      }

      JsonNode actualElement = actual.get(j);

      if (!areStructurallyCompatible(expected, actualElement)) {
        continue;
      }

      List<Difference> candidateDifferences = new ArrayList<>();

      compareNodes(path, expected, actualElement, candidateDifferences);

      if (bestMatch == null || candidateDifferences.size() < bestMatch.getDifferences().size()) {

        bestMatch = new MatchCandidate(j, candidateDifferences);
      }
    }

    return bestMatch;
  }

  private static boolean areStructurallyCompatible(JsonNode expected, JsonNode actual) {

    return (expected.isObject() && actual.isObject()) || (expected.isArray() && actual.isArray());
  }

  private void addUnmatchedElements(
      String path,
      JsonNode expected,
      JsonNode actual,
      boolean[] expectedMatched,
      boolean[] actualMatched,
      List<Difference> differences) {

    for (int i = 0; i < expected.size(); i++) {
      if (!expectedMatched[i]) {
        String elementPath = path + "[" + i + "]";

        if (options.isIgnoredPath(elementPath)) {
          continue;
        }

        differences.add(
            new Difference(
                elementPath,
                DifferenceType.MISSING_ELEMENT,
                toDifferenceValue(expected.get(i)),
                DifferenceValue.missing()));
      }
    }

    for (int j = 0; j < actual.size(); j++) {
      if (!actualMatched[j]) {
        String elementPath = path + "[" + j + "]";

        if (options.isIgnoredPath(elementPath)) {
          continue;
        }

        differences.add(
            new Difference(
                elementPath,
                DifferenceType.UNEXPECTED_ELEMENT,
                DifferenceValue.missing(),
                toDifferenceValue(actual.get(j))));
      }
    }
  }
}
