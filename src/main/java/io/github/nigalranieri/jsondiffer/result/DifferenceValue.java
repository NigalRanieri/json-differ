package io.github.nigalranieri.jsondiffer.result;

import java.util.*;

public final class DifferenceValue {

  private final DifferenceValueType type;
  private final Object value;

  private DifferenceValue(DifferenceValueType type, Object value) {
    this.type = type;
    this.value = makeImmutable(value);
  }

  private static Object makeImmutable(Object value) {
    if (value instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) value;
      Map<Object, Object> copy = new LinkedHashMap<>();

      for (Map.Entry<?, ?> entry : map.entrySet()) {
        copy.put(entry.getKey(), makeImmutable(entry.getValue()));
      }

      return Collections.unmodifiableMap(copy);
    }

    if (value instanceof List) {
      List<?> list = (List<?>) value;
      List<Object> copy = new ArrayList<>();

      for (Object element : list) {
        copy.add(makeImmutable(element));
      }

      return Collections.unmodifiableList(copy);
    }

    return value;
  }

  public static DifferenceValue missing() {
    return new DifferenceValue(DifferenceValueType.MISSING, null);
  }

  public static DifferenceValue ofNull() {
    return new DifferenceValue(DifferenceValueType.NULL, null);
  }

  public static DifferenceValue of(DifferenceValueType type, Object value) {
    Objects.requireNonNull(type, "type");

    if (type == DifferenceValueType.MISSING || type == DifferenceValueType.NULL) {
      throw new IllegalArgumentException("Use missing() or ofNull() for " + type);
    }

    return new DifferenceValue(type, value);
  }

  public DifferenceValueType getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  public boolean isMissing() {
    return type == DifferenceValueType.MISSING;
  }

  public boolean isNull() {
    return type == DifferenceValueType.NULL;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof DifferenceValue)) {
      return false;
    }

    DifferenceValue that = (DifferenceValue) o;

    return type == that.type && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }

  @Override
  public String toString() {
    if (isMissing()) {
      return "<missing>";
    }

    if (isNull()) {
      return "null";
    }

    return formatValue(value);
  }

  private static String formatValue(Object value) {
    if (value == null) {
      return "null";
    }

    if (value instanceof String) {
      return "\"" + escape((String) value) + "\"";
    }

    if (value instanceof Number || value instanceof Boolean) {
      return String.valueOf(value);
    }

    if (value instanceof Map) {
      return formatObject((Map<?, ?>) value);
    }

    if (value instanceof List) {
      return formatArray((List<?>) value);
    }

    return String.valueOf(value);
  }

  private static String formatArray(List<?> values) {
    StringBuilder builder = new StringBuilder("[");

    for (int i = 0; i < values.size(); i++) {
      if (i > 0) {
        builder.append(",");
      }

      builder.append(formatValue(values.get(i)));
    }

    return builder.append("]").toString();
  }

  private static String formatObject(Map<?, ?> values) {
    StringBuilder builder = new StringBuilder("{");

    boolean first = true;

    for (Map.Entry<?, ?> entry : values.entrySet()) {
      if (!first) {
        builder.append(",");
      }

      first = false;

      builder
          .append("\"")
          .append(escape(String.valueOf(entry.getKey())))
          .append("\":")
          .append(formatValue(entry.getValue()));
    }

    return builder.append("}").toString();
  }

  private static String escape(String value) {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");
  }
}
