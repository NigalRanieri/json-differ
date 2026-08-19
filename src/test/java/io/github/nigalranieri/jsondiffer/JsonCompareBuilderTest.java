package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonCompareBuilderTest {

  @ParameterizedTest
  @ValueSource(strings = {"users", "$.users[", "$.users[]", "$.users[abc]", "$.users..name"})
  void shouldRejectInvalidUnorderedArrayPath(String path) {
    assertThrows(
        IllegalArgumentException.class, () -> JsonCompare.builder().ignoreArrayOrder(path));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "$",
        "$.user.name",
        "$.users[0]",
        "$.users[*]",
        "$.*.timestamp",
        "$.**.timestamp",
        "$.groups[*].users"
      })
  void shouldAcceptValidPaths(String path) {
    assertDoesNotThrow(() -> JsonCompare.builder().ignorePath(path));
  }
}
