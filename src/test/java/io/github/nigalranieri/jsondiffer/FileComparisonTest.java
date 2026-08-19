package io.github.nigalranieri.jsondiffer;

import static io.github.nigalranieri.jsondiffer.support.JsonTestResource.path;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.exception.JsonReadException;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class FileComparisonTest {

  @Test
  void shouldCompareJsonFiles() {
    Path expected = path("json/identical-expected.json");
    Path actual = path("json/identical-actual.json");

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldCompareJsonFilesUsingBuilderOptions() {
    Path expected = path("json/different-expected.json");
    Path actual = path("json/different-actual.json");

    ComparisonResult result =
        JsonCompare.builder().ignorePath("$.timestamp").compare(expected, actual);

    assertTrue(result.isEqual());
  }

  @Test
  void shouldThrowJsonReadExceptionWhenFileDoesNotExist() {
    Path missing = Paths.get("does-not-exist.json");
    Path valid = path("json/identical-actual.json");

    assertThrows(JsonReadException.class, () -> JsonCompare.compare(missing, valid));
  }

  @Test
  void shouldThrowInvalidJsonExceptionForMalformedJsonFile() {
    Path invalid = path("json/invalid.json");
    Path valid = path("json/identical-actual.json");

    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare(invalid, valid));
  }

  @Test
  void shouldRejectEmptyJsonFile() {
    Path empty = path("json/empty.json");
    Path valid = path("json/identical-actual.json");

    assertThrows(InvalidJsonException.class, () -> JsonCompare.compare(empty, valid));
  }

  @Test
  void shouldRejectNullExpectedPath() {
    Path valid = path("json/identical-actual.json");

    assertThrows(NullPointerException.class, () -> JsonCompare.compare((Path) null, valid));
  }
}
