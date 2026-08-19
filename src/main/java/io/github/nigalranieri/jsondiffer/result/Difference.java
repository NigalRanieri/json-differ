package io.github.nigalranieri.jsondiffer.result;

public final class Difference {

  private final String path;
  private final DifferenceType type;
  private final Object expected;
  private final Object actual;

  public Difference(String path, DifferenceType type, Object expected, Object actual) {
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

  public Object getExpected() {
    return expected;
  }

  public Object getActual() {
    return actual;
  }
}
