package io.github.nigalranieri.jsondiffer.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for numeric tolerance comparison.
 *
 * <p>A global tolerance can be configured for all numeric values, while path-specific tolerances
 * can override it at matching JSON paths.
 *
 * <p>If multiple path-specific tolerance rules match the same path, the last configured matching
 * tolerance is used.
 */
public final class NumericToleranceConfig {

  private Double global;
  private Map<String, Double> paths = new LinkedHashMap<>();

  /**
   * Returns the global numeric tolerance.
   *
   * @return the global tolerance, or {@code null} when exact numeric comparison is used by default
   */
  public Double getGlobal() {
    return global;
  }

  /**
   * Configures the global numeric tolerance.
   *
   * <p>Validation is applied when the configuration is converted into a comparator.
   *
   * @param global the global numeric tolerance, or {@code null} for exact comparison by default
   */
  public void setGlobal(Double global) {
    this.global = global;
  }

  /**
   * Returns the path-specific numeric tolerances.
   *
   * <p>Iteration order is significant because the last matching path-specific tolerance wins.
   *
   * @return the configured path-to-tolerance mappings
   */
  public Map<String, Double> getPaths() {
    return paths;
  }

  /**
   * Configures path-specific numeric tolerances.
   *
   * <p>A {@code null} value is normalized to an empty ordered map.
   *
   * @param paths the path-to-tolerance mappings
   */
  public void setPaths(Map<String, Double> paths) {
    this.paths = paths == null ? new LinkedHashMap<String, Double>() : paths;
  }
}
