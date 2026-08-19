package io.github.nigalranieri.jsondiffer.internal.comparison;

import io.github.nigalranieri.jsondiffer.result.Difference;
import java.util.List;

final class MatchCandidate {

  private final int actualIndex;
  private final List<Difference> differences;

  MatchCandidate(int actualIndex, List<Difference> differences) {
    this.actualIndex = actualIndex;
    this.differences = differences;
  }

  int getActualIndex() {
    return actualIndex;
  }

  List<Difference> getDifferences() {
    return differences;
  }
}
