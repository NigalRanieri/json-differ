package io.github.nigalranieri.jsondiffer.result;

import java.util.Collections;
import java.util.List;

public final class ComparisonResult {

  private final List<Difference> differences;

  public ComparisonResult(List<Difference> differences) {
    this.differences = Collections.unmodifiableList(differences);
  }

  public boolean isEqual() {
    return differences.isEmpty();
  }

  public List<Difference> getDifferences() {
    return differences;
  }
}
