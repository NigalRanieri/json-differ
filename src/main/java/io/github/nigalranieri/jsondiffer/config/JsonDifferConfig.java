package io.github.nigalranieri.jsondiffer.config;

public final class JsonDifferConfig {

  private ComparisonConfig comparison = new ComparisonConfig();
  private OutputConfig output = new OutputConfig();

  public ComparisonConfig getComparison() {
    return comparison;
  }

  public void setComparison(ComparisonConfig comparison) {
    this.comparison = comparison == null ? new ComparisonConfig() : comparison;
  }

  public OutputConfig getOutput() {
    return output;
  }

  public void setOutput(OutputConfig output) {
    this.output = output == null ? new OutputConfig() : output;
  }
}
