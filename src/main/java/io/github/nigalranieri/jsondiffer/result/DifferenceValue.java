package io.github.nigalranieri.jsondiffer.result;

public final class DifferenceValue {

  private final boolean missing;
  private final Object value;

  private DifferenceValue(boolean missing, Object value) {
    this.missing = missing;
    this.value = value;
  }

  public static DifferenceValue missing() {
    return new DifferenceValue(true, null);
  }

  public static DifferenceValue of(Object value) {
    return new DifferenceValue(false, value);
  }

  public boolean isMissing() {
    return missing;
  }

  public boolean isNull() {
    return !missing && value == null;
  }

  public Object getValue() {
    return value;
  }
}
