package io.github.nigalranieri.jsondiffer.internal.comparison;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    if (expected.isObject() && actual.isObject()) {
      compareObjects(path, expected, actual, differences);
      return;
    }

    if (expected.isArray() && actual.isArray()) {
      compareArrays(path, expected, actual, differences);
      return;
    }

    differences.add(
        new Difference(
            path,
            DifferenceType.VALUE_MISMATCH,
            DifferenceValue.of(toJavaValue(expected)),
            DifferenceValue.of(toJavaValue(actual))));
  }

  private static Object toJavaValue(JsonNode node) {
    if (node.isTextual()) {
      return node.textValue();
    }

    if (node.isNumber()) {
      return node.numberValue();
    }

    if (node.isBoolean()) {
      return node.booleanValue();
    }

    if (node.isNull()) {
      return null;
    }

    return node.toString();
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
        differences.add(
            new Difference(
                fieldPath,
                DifferenceType.MISSING_FIELD,
                DifferenceValue.of(toJavaValue(field.getValue())),
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
        differences.add(
            new Difference(
                fieldPath,
                DifferenceType.UNEXPECTED_FIELD,
                DifferenceValue.missing(),
                DifferenceValue.of(toJavaValue(field.getValue()))));
      }
    }
  }

  private void compareArrays(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    if (options.isIgnoreArrayOrder()) {
      compareUnorderedArrays(path, expected, actual, differences);
      return;
    }

    int commonSize = Math.min(expected.size(), actual.size());

    for (int i = 0; i < commonSize; i++) {
      compareNodes(path + "[" + i + "]", expected.get(i), actual.get(i), differences);
    }

    for (int i = commonSize; i < expected.size(); i++) {
      differences.add(
          new Difference(
              path + "[" + i + "]",
              DifferenceType.MISSING_ELEMENT,
              DifferenceValue.of(toJavaValue(expected.get(i))),
              DifferenceValue.missing()));
    }

    for (int i = commonSize; i < actual.size(); i++) {
      differences.add(
          new Difference(
              path + "[" + i + "]",
              DifferenceType.UNEXPECTED_ELEMENT,
              DifferenceValue.missing(),
              DifferenceValue.of(toJavaValue(actual.get(i)))));
    }
  }

  private void compareUnorderedArrays(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    boolean[] expectedMatched = new boolean[expected.size()];
    boolean[] actualMatched = new boolean[actual.size()];

    matchExactElements(expected, actual, expectedMatched, actualMatched);

    matchSimilarElements(path, expected, actual, expectedMatched, actualMatched, differences);

    addUnmatchedElements(path, expected, actual, expectedMatched, actualMatched, differences);
  }

  private boolean nodesAreEqual(JsonNode expected, JsonNode actual) {

    List<Difference> differences = new ArrayList<>();

    compareNodes("$", expected, actual, differences);

    return differences.isEmpty();
  }

  private void matchExactElements(
      JsonNode expected, JsonNode actual, boolean[] expectedMatched, boolean[] actualMatched) {

    for (int i = 0; i < expected.size(); i++) {
      for (int j = 0; j < actual.size(); j++) {

        if (actualMatched[j]) {
          continue;
        }

        if (nodesAreEqual(expected.get(i), actual.get(j))) {
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

  private static void addUnmatchedElements(
      String path,
      JsonNode expected,
      JsonNode actual,
      boolean[] expectedMatched,
      boolean[] actualMatched,
      List<Difference> differences) {

    for (int i = 0; i < expected.size(); i++) {
      if (!expectedMatched[i]) {
        differences.add(
            new Difference(
                path + "[" + i + "]",
                DifferenceType.MISSING_ELEMENT,
                DifferenceValue.of(toJavaValue(expected.get(i))),
                DifferenceValue.missing()));
      }
    }

    for (int j = 0; j < actual.size(); j++) {
      if (!actualMatched[j]) {
        differences.add(
            new Difference(
                path + "[" + j + "]",
                DifferenceType.UNEXPECTED_ELEMENT,
                DifferenceValue.missing(),
                DifferenceValue.of(toJavaValue(actual.get(j)))));
      }
    }
  }
}
