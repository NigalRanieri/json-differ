package io.github.nigalranieri.jsondiffer.config;

import java.util.ArrayList;
import java.util.List;

public final class ComparisonConfig {

  private List<String> ignorePaths = new ArrayList<>();
  private ArrayOrderConfig arrayOrder = new ArrayOrderConfig();
  private NullAndMissingConfig nullAndMissing = new NullAndMissingConfig();
  private NumericToleranceConfig numericTolerance = new NumericToleranceConfig();
  private IgnoreCaseConfig ignoreCase = new IgnoreCaseConfig();

  public List<String> getIgnorePaths() {
    return ignorePaths;
  }

  public ArrayOrderConfig getArrayOrder() {
    return arrayOrder;
  }

  public NullAndMissingConfig getNullAndMissing() {
    return nullAndMissing;
  }

  public NumericToleranceConfig getNumericTolerance() {
    return numericTolerance;
  }

  public IgnoreCaseConfig getIgnoreCase() {
    return ignoreCase;
  }

  public void setIgnorePaths(List<String> ignorePaths) {
    this.ignorePaths = ignorePaths == null ? new ArrayList<String>() : ignorePaths;
  }

  public void setArrayOrder(ArrayOrderConfig arrayOrder) {
    this.arrayOrder = arrayOrder == null ? new ArrayOrderConfig() : arrayOrder;
  }

  public void setNullAndMissing(NullAndMissingConfig nullAndMissing) {
    this.nullAndMissing = nullAndMissing == null ? new NullAndMissingConfig() : nullAndMissing;
  }

  public void setNumericTolerance(NumericToleranceConfig numericTolerance) {
    this.numericTolerance =
        numericTolerance == null ? new NumericToleranceConfig() : numericTolerance;
  }

  public void setIgnoreCase(IgnoreCaseConfig ignoreCase) {
    this.ignoreCase = ignoreCase == null ? new IgnoreCaseConfig() : ignoreCase;
  }
}
