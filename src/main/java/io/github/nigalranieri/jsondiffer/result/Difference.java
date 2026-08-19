package io.github.nigalranieri.jsondiffer.result;

import java.util.Objects;

public final class Difference {

  private final String path;
  private final DifferenceType type;
  private final DifferenceValue expected;
  private final DifferenceValue actual;

  public Difference(
      String path, DifferenceType type, DifferenceValue expected, DifferenceValue actual) {
    this.path = Objects.requireNonNull(path, "path");
    this.type = Objects.requireNonNull(type, "type");
    this.expected = Objects.requireNonNull(expected, "expected");
    this.actual = Objects.requireNonNull(actual, "actual");
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Difference)) {
      return false;
    }

    Difference that = (Difference) o;

    return Objects.equals(path, that.path)
        && type == that.type
        && Objects.equals(expected, that.expected)
        && Objects.equals(actual, that.actual);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, type, expected, actual);
  }
}
