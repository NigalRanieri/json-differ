package io.github.nigalranieri.jsondiffer.config;

/**
 * Root configuration object for json-differ.
 *
 * <p>The configuration is divided into comparison settings, which control JSON equality, and output
 * settings, which control how comparison results are rendered.
 *
 * <p>Omitted or explicitly {@code null} sections are normalized to their default configuration.
 */
public final class JsonDifferConfig {

  private ComparisonConfig comparison = new ComparisonConfig();
  private OutputConfig output = new OutputConfig();

  /**
   * Returns the comparison configuration.
   *
   * @return the comparison configuration
   */
  public ComparisonConfig getComparison() {
    return comparison;
  }

  /**
   * Configures comparison behavior.
   *
   * <p>A {@code null} value is normalized to the default comparison configuration.
   *
   * @param comparison the comparison configuration
   */
  public void setComparison(ComparisonConfig comparison) {
    this.comparison = comparison == null ? new ComparisonConfig() : comparison;
  }

  /**
   * Returns the output configuration.
   *
   * @return the output configuration
   */
  public OutputConfig getOutput() {
    return output;
  }

  /**
   * Configures result rendering.
   *
   * <p>A {@code null} value is normalized to the default output configuration.
   *
   * @param output the output configuration
   */
  public void setOutput(OutputConfig output) {
    this.output = output == null ? new OutputConfig() : output;
  }
}
