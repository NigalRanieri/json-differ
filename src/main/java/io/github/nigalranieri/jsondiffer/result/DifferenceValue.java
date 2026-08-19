package io.github.nigalranieri.jsondiffer.result;

public final class DifferenceValue {

  private final DifferenceValueType type;
  private final Object value;

  private DifferenceValue(DifferenceValueType type, Object value) {
    this.type = type;
    this.value = value;
  }

  public static DifferenceValue missing() {
    return new DifferenceValue(DifferenceValueType.MISSING, null);
  }

  public static DifferenceValue ofNull() {
    return new DifferenceValue(DifferenceValueType.NULL, null);
  }

  public static DifferenceValue of(DifferenceValueType type, Object value) {
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
}
