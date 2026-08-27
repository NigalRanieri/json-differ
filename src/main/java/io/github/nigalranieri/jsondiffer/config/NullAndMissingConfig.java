package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

public final class NullAndMissingConfig {

  private boolean equalGlobally;
  private List<String> equalAt = new ArrayList<>();

  public boolean isEqualGlobally() {
    return equalGlobally;
  }

  public void setEqualGlobally(boolean equalGlobally) {
    this.equalGlobally = equalGlobally;
  }

  public List<String> getEqualAt() {
    return equalAt;
  }

  public void setEqualAt(List<String> equalAt) {
    this.equalAt = equalAt == null ? new ArrayList<String>() : equalAt;
  }
}
