package io.github.nigalranieri.jsondiffer.internal.path;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PathMatcherTest {

  private final PathMatcher pathMatcher = new PathMatcher();

  @Test
  void shouldMatchExactPath() {
    assertTrue(pathMatcher.matches("$.users[0].name", "$.users[0].name"));
  }

  @Test
  void shouldMatchArrayWildcard() {
    assertTrue(pathMatcher.matches("$.users[*].name", "$.users[3].name"));
  }

  @Test
  void shouldMatchPropertyWildcard() {
    assertTrue(pathMatcher.matches("$.metadata.*", "$.metadata.timestamp"));
  }

  @Test
  void shouldNotMatchDifferentPath() {
    assertFalse(pathMatcher.matches("$.users[*].timestamp", "$.users[0].name"));
  }

  @Test
  void shouldMatchRecursiveWildcardAtRoot() {
    assertTrue(pathMatcher.matches("$.**.timestamp", "$.timestamp"));
  }

  @Test
  void shouldMatchRecursiveWildcardAtAnyDepth() {
    assertTrue(pathMatcher.matches("$.**.timestamp", "$.metadata.timestamp"));
    assertTrue(pathMatcher.matches("$.**.timestamp", "$.users[0].metadata.timestamp"));
  }

  @Test
  void shouldNotMatchRecursiveWildcardWhenSuffixDiffers() {
    assertFalse(pathMatcher.matches("$.**.timestamp", "$.metadata.createdAt"));
  }

  @Test
  void shouldNotMatchPastTheConfiguredSuffix() {
    assertFalse(pathMatcher.matches("$.**.timestamp", "$.timestamp.value"));
  }

  @Test
  void matchesOrIsAncestorReturnsTrueForExactMatch() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.user.name", "$.user.name"));
  }

  @Test
  void matchesOrIsAncestorReturnsTrueForAncestor() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.user.name", "$.user"));
  }

  @Test
  void matchesOrIsAncestorReturnsTrueForDescendantOfIncludedPath() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.user", "$.user.name"));
  }

  @Test
  void matchesOrIsAncestorReturnsFalseForUnrelatedPath() {
    assertFalse(pathMatcher.matchesOrIsAncestor("$.user.name", "$.metadata"));
  }

  @Test
  void matchesOrIsAncestorSupportsArrayWildcardAncestors() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.users[*].name", "$.users"));
    assertTrue(pathMatcher.matchesOrIsAncestor("$.users[*].name", "$.users[0]"));
  }

  @Test
  void matchesOrIsAncestorSupportsWildcardDescendants() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.users[*]", "$.users[0].name"));
  }

  @Test
  void matchesOrIsAncestorSupportsRecursiveWildcardAncestors() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.**.email", "$.user"));
    assertTrue(pathMatcher.matchesOrIsAncestor("$.**.email", "$.user.contact"));
  }

  @Test
  void matchesOrIsAncestorReturnsTrueForRoot() {
    assertTrue(pathMatcher.matchesOrIsAncestor("$.user.name", "$"));
  }

  @Test
  void matchesOrIsDescendantReturnsTrueForExactMatch() {
    assertTrue(pathMatcher.matchesOrIsDescendant("$.user", "$.user"));
  }

  @Test
  void matchesOrIsDescendantReturnsTrueForDescendant() {
    assertTrue(pathMatcher.matchesOrIsDescendant("$.user", "$.user.name"));
  }

  @Test
  void matchesOrIsDescendantReturnsFalseForAncestor() {
    assertFalse(pathMatcher.matchesOrIsDescendant("$.user.name", "$.user"));
  }

  @Test
  void matchesOrIsDescendantSupportsArrayWildcard() {
    assertTrue(pathMatcher.matchesOrIsDescendant("$.users[*]", "$.users[0].name"));
  }

  @Test
  void matchesOrIsDescendantSupportsRecursiveWildcard() {
    assertTrue(pathMatcher.matchesOrIsDescendant("$.**.email", "$.user.email"));
    assertTrue(pathMatcher.matchesOrIsDescendant("$.**.email", "$.user.contact.email"));
    assertFalse(pathMatcher.matchesOrIsDescendant("$.**.email", "$.version"));
  }
}
