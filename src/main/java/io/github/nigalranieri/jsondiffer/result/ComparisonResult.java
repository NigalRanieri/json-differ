package io.github.nigalranieri.jsondiffer.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ComparisonResult {

  private final List<Difference> differences;

  public ComparisonResult(List<Difference> differences) {
    Objects.requireNonNull(differences, "differences");
    this.differences = Collections.unmodifiableList(new ArrayList<>(differences));
  }

  public boolean isEqual() {
    return differences.isEmpty();
  }

  public List<Difference> getDifferences() {
    return differences;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof ComparisonResult)) {
      return false;
    }

    ComparisonResult that = (ComparisonResult) o;

    return Objects.equals(differences, that.differences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(differences);
  }
}
