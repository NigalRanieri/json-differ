package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

public final class IgnoreCaseConfig {

  private boolean globally;
  private List<String> paths = new ArrayList<>();

  public boolean isGlobally() {
    return globally;
  }

  public void setGlobally(boolean globally) {
    this.globally = globally;
  }

  public List<String> getPaths() {
    return paths;
  }

  public void setPaths(List<String> paths) {
    this.paths = paths == null ? new ArrayList<String>() : paths;
  }
}
