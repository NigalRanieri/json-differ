package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for JSON comparison behavior.
 *
 * <p>All comparison rules are strict by default. Individual behaviors can be relaxed globally or at
 * matching JSON paths.
 */
public final class ComparisonConfig {

  private List<String> ignorePaths = new ArrayList<>();
  private ArrayOrderConfig arrayOrder = new ArrayOrderConfig();
  private NullAndMissingConfig nullAndMissing = new NullAndMissingConfig();
  private NumericToleranceConfig numericTolerance = new NumericToleranceConfig();
  private IgnoreCaseConfig ignoreCase = new IgnoreCaseConfig();

  /**
   * Returns the paths whose differences should be ignored.
   *
   * @return the ignored path patterns
   */
  public List<String> getIgnorePaths() {
    return ignorePaths;
  }

  /**
   * Returns the array-order configuration.
   *
   * @return the array-order configuration
   */
  public ArrayOrderConfig getArrayOrder() {
    return arrayOrder;
  }

  /**
   * Returns the null/missing equivalence configuration.
   *
   * @return the null/missing configuration
   */
  public NullAndMissingConfig getNullAndMissing() {
    return nullAndMissing;
  }

  /**
   * Returns the numeric tolerance configuration.
   *
   * @return the numeric tolerance configuration
   */
  public NumericToleranceConfig getNumericTolerance() {
    return numericTolerance;
  }

  /**
   * Returns the case-insensitive string comparison configuration.
   *
   * @return the case-insensitive comparison configuration
   */
  public IgnoreCaseConfig getIgnoreCase() {
    return ignoreCase;
  }

  /**
   * Configures the paths whose differences should be ignored.
   *
   * <p>A {@code null} value is normalized to an empty list.
   *
   * @param ignorePaths the ignored path patterns
   */
  public void setIgnorePaths(List<String> ignorePaths) {
    this.ignorePaths = ignorePaths == null ? new ArrayList<String>() : ignorePaths;
  }

  /**
   * Configures array-order comparison.
   *
   * <p>A {@code null} value is normalized to the default array-order configuration.
   *
   * @param arrayOrder the array-order configuration
   */
  public void setArrayOrder(ArrayOrderConfig arrayOrder) {
    this.arrayOrder = arrayOrder == null ? new ArrayOrderConfig() : arrayOrder;
  }

  /**
   * Configures null/missing equivalence.
   *
   * <p>A {@code null} value is normalized to the default null/missing configuration.
   *
   * @param nullAndMissing the null/missing configuration
   */
  public void setNullAndMissing(NullAndMissingConfig nullAndMissing) {
    this.nullAndMissing = nullAndMissing == null ? new NullAndMissingConfig() : nullAndMissing;
  }

  /**
   * Configures numeric tolerance comparison.
   *
   * <p>A {@code null} value is normalized to the default numeric tolerance configuration.
   *
   * @param numericTolerance the numeric tolerance configuration
   */
  public void setNumericTolerance(NumericToleranceConfig numericTolerance) {
    this.numericTolerance =
        numericTolerance == null ? new NumericToleranceConfig() : numericTolerance;
  }

  /**
   * Configures case-insensitive string comparison.
   *
   * <p>A {@code null} value is normalized to the default case-insensitive comparison configuration.
   *
   * @param ignoreCase the case-insensitive comparison configuration
   */
  public void setIgnoreCase(IgnoreCaseConfig ignoreCase) {
    this.ignoreCase = ignoreCase == null ? new IgnoreCaseConfig() : ignoreCase;
  }
}
