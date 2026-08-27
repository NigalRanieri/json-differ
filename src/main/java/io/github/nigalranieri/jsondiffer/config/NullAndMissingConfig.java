package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for treating JSON {@code null} values and missing object properties as equal.
 *
 * <p>The behavior can be enabled globally or only for properties matching specific JSON path
 * patterns.
 *
 * <p>Path-specific rules support the same path syntax as the programmatic comparison API, including
 * {@code *}, {@code [*]}, and recursive {@code **} wildcards.
 */
public final class NullAndMissingConfig {

  private boolean equalGlobally;
  private List<String> equalAt = new ArrayList<>();

  /**
   * Indicates whether {@code null} and missing object properties are treated as equal globally.
   *
   * @return {@code true} when the behavior is enabled globally
   */
  public boolean isEqualGlobally() {
    return equalGlobally;
  }

  /**
   * Configures whether {@code null} and missing object properties are treated as equal globally.
   *
   * @param equalGlobally {@code true} to enable the behavior globally
   */
  public void setEqualGlobally(boolean equalGlobally) {
    this.equalGlobally = equalGlobally;
  }

  /**
   * Returns the path patterns where {@code null} and missing object properties are treated as
   * equal.
   *
   * @return the configured path patterns
   */
  public List<String> getEqualAt() {
    return equalAt;
  }

  /**
   * Configures the path patterns where {@code null} and missing object properties are treated as
   * equal.
   *
   * <p>A {@code null} value is normalized to an empty list.
   *
   * @param equalAt the path patterns where the behavior should apply
   */
  public void setEqualAt(List<String> equalAt) {
    this.equalAt = equalAt == null ? new ArrayList<String>() : equalAt;
  }
}
