package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for array-order comparison.
 *
 * <p>Array order can be ignored globally or only for arrays matching specific JSON path patterns.
 *
 * <p>Path-specific rules support the same path syntax as the programmatic comparison API, including
 * {@code *}, {@code [*]}, and recursive {@code **} wildcards.
 */
public final class ArrayOrderConfig {

  private boolean ignoreGlobally;
  private List<String> ignoreAt = new ArrayList<>();

  /**
   * Indicates whether array order is ignored globally.
   *
   * @return {@code true} when all arrays are compared without considering element order
   */
  public boolean isIgnoreGlobally() {
    return ignoreGlobally;
  }

  /**
   * Configures whether array order is ignored globally.
   *
   * @param ignoreGlobally {@code true} to ignore element order for all arrays
   */
  public void setIgnoreGlobally(boolean ignoreGlobally) {
    this.ignoreGlobally = ignoreGlobally;
  }

  /**
   * Returns the path patterns where array order is ignored.
   *
   * @return the configured path patterns
   */
  public List<String> getIgnoreAt() {
    return ignoreAt;
  }

  /**
   * Configures the path patterns where array order is ignored.
   *
   * <p>A {@code null} value is normalized to an empty list.
   *
   * @param ignoreAt the path patterns where array order should be ignored
   */
  public void setIgnoreAt(List<String> ignoreAt) {
    this.ignoreAt = ignoreAt == null ? new ArrayList<String>() : ignoreAt;
  }
}
