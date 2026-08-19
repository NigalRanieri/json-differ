package io.github.nigalranieri.jsondiffer.result;

public final class Difference {

  private final String path;
  private final DifferenceType type;
  private final DifferenceValue expected;
  private final DifferenceValue actual;

  public Difference(
      String path, DifferenceType type, DifferenceValue expected, DifferenceValue actual) {
    this.path = path;
    this.type = type;
    this.expected = expected;
    this.actual = actual;
  }

  public String getPath() {
    return path;
  }

  public DifferenceType getType() {
    return type;
  }

  public DifferenceValue getExpected() {
    return expected;
  }

  public DifferenceValue getActual() {
    return actual;
  }
}
