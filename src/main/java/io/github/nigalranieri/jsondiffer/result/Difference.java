package io.github.nigalranieri.jsondiffer.result;

import java.util.Objects;

/**
 * Describes a single difference detected between two JSON documents.
 *
 * <p>A difference records where the mismatch occurred, the kind of difference, and the values
 * observed on the expected and actual sides.
 *
 * <p>Missing values are represented explicitly through {@link DifferenceValue#missing()} rather
 * than Java {@code null}.
 */
public final class Difference {

  private final String path;
  private final DifferenceType type;
  private final DifferenceValue expected;
  private final DifferenceValue actual;

  /**
   * Creates a difference.
   *
   * @param path the JSON path at which the difference was detected
   * @param type the type of difference
   * @param expected the expected-side value
   * @param actual the actual-side value
   * @throws NullPointerException if any argument is {@code null}
   */
  public Difference(
      String path, DifferenceType type, DifferenceValue expected, DifferenceValue actual) {
    this.path = Objects.requireNonNull(path, "path");
    this.type = Objects.requireNonNull(type, "type");
    this.expected = Objects.requireNonNull(expected, "expected");
    this.actual = Objects.requireNonNull(actual, "actual");
  }

  /**
   * Returns the JSON path where the difference occurred.
   *
   * @return the difference path
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns the kind of difference that was detected.
   *
   * @return the difference type
   */
  public DifferenceType getType() {
    return type;
  }

  /**
   * Returns the value from the expected JSON document.
   *
   * <p>If the value is absent, the returned {@link DifferenceValue} has type {@link
   * DifferenceValueType#MISSING}.
   *
   * @return the expected-side value
   */
  public DifferenceValue getExpected() {
    return expected;
  }

  /**
   * Returns the value from the actual JSON document.
   *
   * <p>If the value is absent, the returned {@link DifferenceValue} has type {@link
   * DifferenceValueType#MISSING}.
   *
   * @return the actual-side value
   */
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

  /**
   * Returns a concise human-readable representation of this difference.
   *
   * @return the formatted difference
   */
  @Override
  public String toString() {
    return type + " at " + path + ": expected=" + expected + ", actual=" + actual;
  }
}
