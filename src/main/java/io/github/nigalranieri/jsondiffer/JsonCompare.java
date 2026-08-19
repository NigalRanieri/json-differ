package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.internal.ComparisonOptions;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JsonCompare {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonCompare() {}

  public static JsonCompareBuilder builder() {
    return new JsonCompareBuilder();
  }

  public static boolean equals(String first, String second) {
    return compare(first, second).isEqual();
  }

  public static ComparisonResult compare(String expected, String actual) {
    return compare(expected, actual, new ComparisonOptions(false));
  }

  static ComparisonResult compare(String expected, String actual, ComparisonOptions options) {

    JsonNode expectedNode = parse(expected);
    JsonNode actualNode = parse(actual);

    List<Difference> differences = new ArrayList<>();

    compareNodes("$", expectedNode, actualNode, differences, options);

    return new ComparisonResult(differences);
  }

  private static void compareNodes(
      String path,
      JsonNode expected,
      JsonNode actual,
      List<Difference> differences,
      ComparisonOptions options) {

    if (expected.equals(actual)) {
      return;
    }

    if (expected.isObject() && actual.isObject()) {
      compareObjects(path, expected, actual, differences, options);
      return;
    }

    if (expected.isArray() && actual.isArray()) {
      compareArrays(path, expected, actual, differences, options);
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

  private static <T> Iterable<T> iterable(final java.util.Iterator<T> iterator) {
    return () -> iterator;
  }

  private static JsonNode parse(String json) {
    try {
      return OBJECT_MAPPER.readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException("Invalid JSON", e);
    }
  }

  private static void compareObjects(
      String path,
      JsonNode expected,
      JsonNode actual,
      List<Difference> differences,
      ComparisonOptions options) {

    Iterator<Map.Entry<String, JsonNode>> expectedFields = expected.fields();

    while (expectedFields.hasNext()) {
      Map.Entry<String, JsonNode> field = expectedFields.next();
      String fieldName = field.getKey();
      String fieldPath = path + "." + fieldName;

      if (!actual.has(fieldName)) {
        differences.add(
            new Difference(
                fieldPath,
                DifferenceType.MISSING_FIELD,
                DifferenceValue.of(toJavaValue(field.getValue())),
                DifferenceValue.missing()));
        continue;
      }

      compareNodes(fieldPath, field.getValue(), actual.get(fieldName), differences, options);
    }

    Iterator<Map.Entry<String, JsonNode>> actualFields = actual.fields();

    while (actualFields.hasNext()) {
      Map.Entry<String, JsonNode> field = actualFields.next();
      String fieldName = field.getKey();

      if (!expected.has(fieldName)) {
        differences.add(
            new Difference(
                path + "." + fieldName,
                DifferenceType.UNEXPECTED_FIELD,
                DifferenceValue.missing(),
                DifferenceValue.of(toJavaValue(field.getValue()))));
      }
    }
  }

  private static void compareArrays(
      String path,
      JsonNode expected,
      JsonNode actual,
      List<Difference> differences,
      ComparisonOptions options) {

    if (options.isIgnoreArrayOrder()) {
      compareUnorderedArrays(path, expected, actual, differences, options);
      return;
    }

    int commonSize = Math.min(expected.size(), actual.size());

    for (int i = 0; i < commonSize; i++) {
      compareNodes(path + "[" + i + "]", expected.get(i), actual.get(i), differences, options);
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

  private static void compareUnorderedArrays(
      String path,
      JsonNode expected,
      JsonNode actual,
      List<Difference> differences,
      ComparisonOptions options) {

    boolean[] matched = new boolean[actual.size()];

    for (int i = 0; i < expected.size(); i++) {
      JsonNode expectedElement = expected.get(i);

      boolean foundMatch = false;

      for (int j = 0; j < actual.size(); j++) {
        if (matched[j]) {
          continue;
        }

        if (nodesAreEqual(expectedElement, actual.get(j), options)) {
          matched[j] = true;
          foundMatch = true;
          break;
        }
      }

      if (!foundMatch) {
        differences.add(
            new Difference(
                path + "[" + i + "]",
                DifferenceType.MISSING_ELEMENT,
                DifferenceValue.of(toJavaValue(expectedElement)),
                DifferenceValue.missing()));
      }
    }

    for (int j = 0; j < actual.size(); j++) {
      if (!matched[j]) {
        differences.add(
            new Difference(
                path + "[" + j + "]",
                DifferenceType.UNEXPECTED_ELEMENT,
                DifferenceValue.missing(),
                DifferenceValue.of(toJavaValue(actual.get(j)))));
      }
    }
  }

  private static boolean nodesAreEqual(
      JsonNode expected, JsonNode actual, ComparisonOptions options) {

    List<Difference> differences = new ArrayList<>();

    compareNodes("$", expected, actual, differences, options);

    return differences.isEmpty();
  }
}
