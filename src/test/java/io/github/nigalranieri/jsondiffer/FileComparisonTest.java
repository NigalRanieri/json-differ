package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.exception.JsonReadException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class FileComparisonTest {

  private Path resourcePath(String resource) {
    try {
      return Paths.get(
          Objects.requireNonNull(getClass().getClassLoader().getResource(resource)).toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldCompareJsonFiles() {
    Path expected = resourcePath("json/identical-expected.json");
    Path actual = resourcePath("json/identical-actual.json");

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldCompareJsonFilesUsingBuilderOptions() {
    Path expected = resourcePath("json/different-expected.json");
    Path actual = resourcePath("json/different-actual.json");

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldThrowJsonReadExceptionWhenFileDoesNotExist() {
    Path missing = Paths.get("does-not-exist.json");
    Path valid = resourcePath("json/identical-actual.json");

    assertThrows(JsonReadException.class, () -> JsonCompare.compare(missing, valid));
  }

  @Test
  void shouldThrowInvalidJsonExceptionForMalformedJsonFile() {
    Path invalid = resourcePath("json/invalid.json");
    Path valid = resourcePath("json/identical-actual.json");

    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare(invalid, valid));
  }

  @Test
  void shouldRejectEmptyJsonString() {
    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare("", "{}"));
  }

  @Test
  void shouldRejectEmptyJsonFile() {
    Path empty = resourcePath("json/empty.json");
    Path valid = resourcePath("json/identical-actual.json");

    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare(empty, valid));
  }

  @Test
  void shouldRejectNullExpectedPath() {
    Path valid = resourcePath("json/identical-actual.json");

    assertThrows(NullPointerException.class, () -> JsonCompare.compare((Path) null, valid));
  }
}
