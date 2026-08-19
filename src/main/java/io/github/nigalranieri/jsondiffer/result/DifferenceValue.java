package io.github.nigalranieri.jsondiffer.result;

import java.util.*;

/**
 * Immutable representation of a value participating in a JSON difference.
 *
 * <p>A difference value preserves the JSON value type through {@link DifferenceValueType}. JSON
 * objects are exposed as immutable {@link Map} instances and JSON arrays as immutable {@link List}
 * instances, so callers do not need to depend on the library's internal JSON parser.
 *
 * <p>Missing values and JSON {@code null} are represented as distinct states. Use {@link
 * #missing()} for an absent value and {@link #ofNull()} for an explicit JSON {@code null}.
 */
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

  /**
   * Creates a value representing an absent JSON field or array element.
   *
   * @return a missing difference value
   */
  public static DifferenceValue missing() {
    return new DifferenceValue(DifferenceValueType.MISSING, null);
  }

  /**
   * Creates a value representing an explicit JSON {@code null}.
   *
   * @return a JSON-null difference value
   */
  public static DifferenceValue ofNull() {
    return new DifferenceValue(DifferenceValueType.NULL, null);
  }

  /**
   * Creates a typed difference value.
   *
   * <p>The supplied Java value must be compatible with the requested type:
   *
   * <ul>
   *   <li>{@link DifferenceValueType#STRING} requires a {@link String}
   *   <li>{@link DifferenceValueType#NUMBER} requires a {@link Number}
   *   <li>{@link DifferenceValueType#BOOLEAN} requires a {@link Boolean}
   *   <li>{@link DifferenceValueType#OBJECT} requires a {@link Map}
   *   <li>{@link DifferenceValueType#ARRAY} requires a {@link List}
   * </ul>
   *
   * <p>Use {@link #missing()} and {@link #ofNull()} for the special {@link
   * DifferenceValueType#MISSING} and {@link DifferenceValueType#NULL} states.
   *
   * @param type the JSON value type
   * @param value the corresponding Java representation
   * @return a typed immutable difference value
   * @throws NullPointerException if {@code type} or {@code value} is {@code null}
   * @throws IllegalArgumentException if the value is incompatible with {@code type}, or if {@code
   *     MISSING} or {@code NULL} is passed to this factory
   */
  public static DifferenceValue of(DifferenceValueType type, Object value) {
    Objects.requireNonNull(type, "type");

    if (type == DifferenceValueType.MISSING || type == DifferenceValueType.NULL) {
      throw new IllegalArgumentException("Use missing() or ofNull() for " + type);
    }

    Objects.requireNonNull(value, "value");

    if (!isCompatible(type, value)) {
      throw new IllegalArgumentException("Value is not compatible with type " + type);
    }

    return new DifferenceValue(type, value);
  }

  private static boolean isCompatible(DifferenceValueType type, Object value) {

    switch (type) {
      case STRING:
        return value instanceof String;
      case NUMBER:
        return value instanceof Number;
      case BOOLEAN:
        return value instanceof Boolean;
      case OBJECT:
        return value instanceof Map;
      case ARRAY:
        return value instanceof List;
      default:
        return false;
    }
  }

  /**
   * Returns the JSON type represented by this value.
   *
   * @return the difference value type
   */
  public DifferenceValueType getType() {
    return type;
  }

  /**
   * Returns the Java representation of this JSON value.
   *
   * <p>For {@link DifferenceValueType#MISSING} and {@link DifferenceValueType#NULL}, this method
   * returns {@code null}. Object values are represented as immutable maps and array values as
   * immutable lists.
   *
   * @return the represented value, or {@code null} for missing and JSON-null values
   */
  public Object getValue() {
    return value;
  }

  /**
   * Indicates whether this value represents an absent JSON value.
   *
   * @return {@code true} if this value is missing
   */
  public boolean isMissing() {
    return type == DifferenceValueType.MISSING;
  }

  /**
   * Indicates whether this value represents an explicit JSON {@code null}.
   *
   * @return {@code true} if this value is JSON {@code null}
   */
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

  /**
   * Returns a JSON-like textual representation of this value.
   *
   * <p>Missing values are rendered as {@code <missing>}, while JSON {@code null} is rendered as
   * {@code null}.
   *
   * @return the formatted value
   */
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
