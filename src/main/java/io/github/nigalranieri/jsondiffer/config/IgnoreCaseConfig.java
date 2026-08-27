package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for case-insensitive string comparison.
 *
 * <p>Case can be ignored globally or only for string values matching specific JSON path patterns.
 *
 * <p>Path-specific rules support the same path syntax as the programmatic comparison API, including
 * {@code *}, {@code [*]}, and recursive {@code **} wildcards.
 */
public final class IgnoreCaseConfig {

  private boolean globally;
  private List<String> paths = new ArrayList<>();

  /**
   * Indicates whether string case is ignored globally.
   *
   * @return {@code true} when all string values are compared case-insensitively
   */
  public boolean isGlobally() {
    return globally;
  }

  /**
   * Configures whether string case is ignored globally.
   *
   * @param globally {@code true} to compare all string values case-insensitively
   */
  public void setGlobally(boolean globally) {
    this.globally = globally;
  }

  /**
   * Returns the path patterns where string case is ignored.
   *
   * @return the configured path patterns
   */
  public List<String> getPaths() {
    return paths;
  }

  /**
   * Configures the path patterns where string case is ignored.
   *
   * <p>A {@code null} value is normalized to an empty list.
   *
   * @param paths the path patterns where case should be ignored
   */
  public void setPaths(List<String> paths) {
    this.paths = paths == null ? new ArrayList<String>() : paths;
  }
}
