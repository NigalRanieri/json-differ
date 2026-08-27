package io.github.nigalranieri.jsondiffer.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class NumericToleranceConfig {

  private Double global;
  private Map<String, Double> paths = new LinkedHashMap<>();

  public Double getGlobal() {
    return global;
  }

  public void setGlobal(Double global) {
    this.global = global;
  }

  public Map<String, Double> getPaths() {
    return paths;
  }

  public void setPaths(Map<String, Double> paths) {
    this.paths = paths == null ? new LinkedHashMap<String, Double>() : paths;
  }
}
