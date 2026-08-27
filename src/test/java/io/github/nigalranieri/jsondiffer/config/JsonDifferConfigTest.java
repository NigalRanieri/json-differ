package io.github.nigalranieri.jsondiffer.config;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class JsonDifferConfigTest {

  @Test
  void acceptsNonNullComparisonConfiguration() {
    JsonDifferConfig config = new JsonDifferConfig();
    ComparisonConfig comparison = new ComparisonConfig();

    config.setComparison(comparison);

    assertSame(comparison, config.getComparison());
  }
}
