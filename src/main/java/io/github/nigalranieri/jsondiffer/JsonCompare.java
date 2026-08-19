package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.Difference;
import io.github.nigalranieri.jsondiffer.result.DifferenceType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JsonCompare {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private JsonCompare() {}

  public static boolean equals(String first, String second) {
    return compare(first, second).isEqual();
  }

  public static ComparisonResult compare(String expected, String actual) {
    JsonNode expectedNode = parse(expected);
    JsonNode actualNode = parse(actual);

    List<Difference> differences = new ArrayList<>();

    compareNodes("$", expectedNode, actualNode, differences);

    return new ComparisonResult(differences);
  }

  private static void compareNodes(
      String path, JsonNode expected, JsonNode actual, List<Difference> differences) {

    if (expected.equals(actual)) {
      return;
    }

    if (expected.isObject() && actual.isObject()) {
      for (Map.Entry<String, JsonNode> field : iterable(expected.fields())) {
        String fieldName = field.getKey();

        if (actual.has(fieldName)) {
          compareNodes(
              path + "." + fieldName, field.getValue(), actual.get(fieldName), differences);
        }
      }

      return;
    }

    differences.add(
        new Difference(
            path, DifferenceType.VALUE_MISMATCH, toJavaValue(expected), toJavaValue(actual)));
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
}
