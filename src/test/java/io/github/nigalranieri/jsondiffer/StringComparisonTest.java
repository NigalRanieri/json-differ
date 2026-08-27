package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import org.junit.jupiter.api.Test;

class StringComparisonTest {

  @Test
  void stringComparisonIsCaseSensitiveByDefault() {
    ComparisonResult result = JsonCompare.compare("{\"name\":\"Alice\"}", "{\"name\":\"alice\"}");

    assertFalse(result.isEqual());
  }

  @Test
  void ignoresStringCaseGloballyWhenConfigured() {
    ComparisonResult result =
        JsonCompare.builder().ignoreCase().compare("{\"name\":\"Alice\"}", "{\"name\":\"alice\"}");

    assertTrue(result.isEqual());
  }

  @Test
  void ignoresStringCaseAtConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase("$.name")
            .compare("{\"name\":\"Alice\"}", "{\"name\":\"alice\"}");

    assertTrue(result.isEqual());
  }

  @Test
  void doesNotIgnoreStringCaseOutsideConfiguredPath() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase("$.name")
            .compare(
                "{\"name\":\"Alice\",\"city\":\"Milan\"}",
                "{\"name\":\"alice\",\"city\":\"milan\"}");

    assertFalse(result.isEqual());
  }

  @Test
  void globalIgnoreCaseAppliesToAllStringValues() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase()
            .compare(
                "{\"name\":\"Alice\",\"city\":\"Milan\"}",
                "{\"name\":\"alice\",\"city\":\"milan\"}");

    assertTrue(result.isEqual());
  }

  @Test
  void ignoreCaseDoesNotApplyToObjectFieldNames() {
    ComparisonResult result =
        JsonCompare.builder().ignoreCase().compare("{\"name\":\"Alice\"}", "{\"Name\":\"Alice\"}");

    assertFalse(result.isEqual());
  }

  @Test
  void ignoresStringCaseWithObjectWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase("$.user.*")
            .compare(
                "{\"user\":{\"firstName\":\"Alice\",\"lastName\":\"Smith\"}}",
                "{\"user\":{\"firstName\":\"alice\",\"lastName\":\"smith\"}}");

    assertTrue(result.isEqual());
  }

  @Test
  void ignoresStringCaseWithArrayWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase("$.users[*].name")
            .compare(
                "{\"users\":[{\"name\":\"Alice\"},{\"name\":\"Bob\"}]}",
                "{\"users\":[{\"name\":\"alice\"},{\"name\":\"bob\"}]}");

    assertTrue(result.isEqual());
  }

  @Test
  void ignoresStringCaseWithRecursiveWildcard() {
    ComparisonResult result =
        JsonCompare.builder()
            .ignoreCase("$.**.name")
            .compare(
                "{\"user\":{\"name\":\"Alice\"},\"nested\":{\"user\":{\"name\":\"Bob\"}}}",
                "{\"user\":{\"name\":\"alice\"},\"nested\":{\"user\":{\"name\":\"bob\"}}}");

    assertTrue(result.isEqual());
  }
}
