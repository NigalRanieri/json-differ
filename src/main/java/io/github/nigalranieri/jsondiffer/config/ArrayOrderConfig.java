package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

public final class ArrayOrderConfig {

  private boolean ignoreGlobally;
  private List<String> ignoreAt = new ArrayList<>();

  public boolean isIgnoreGlobally() {
    return ignoreGlobally;
  }

  public void setIgnoreGlobally(boolean ignoreGlobally) {
    this.ignoreGlobally = ignoreGlobally;
  }

  public List<String> getIgnoreAt() {
    return ignoreAt;
  }

  public void setIgnoreAt(List<String> ignoreAt) {
    this.ignoreAt = ignoreAt == null ? new ArrayList<String>() : ignoreAt;
  }
}
