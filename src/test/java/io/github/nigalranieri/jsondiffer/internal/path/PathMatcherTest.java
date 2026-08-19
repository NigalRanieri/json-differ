package io.github.nigalranieri.jsondiffer.internal.path;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PathMatcherTest {

  private final PathMatcher matcher = new PathMatcher();

  @Test
  void shouldMatchExactPath() {
    assertTrue(matcher.matches("$.users[0].name", "$.users[0].name"));
  }

  @Test
  void shouldMatchArrayWildcard() {
    assertTrue(matcher.matches("$.users[*].name", "$.users[3].name"));
  }

  @Test
  void shouldMatchPropertyWildcard() {
    assertTrue(matcher.matches("$.metadata.*", "$.metadata.timestamp"));
  }

  @Test
  void shouldNotMatchDifferentPath() {
    assertFalse(matcher.matches("$.users[*].timestamp", "$.users[0].name"));
  }
}
