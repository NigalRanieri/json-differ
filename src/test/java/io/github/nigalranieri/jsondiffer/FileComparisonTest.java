package io.github.nigalranieri.jsondiffer;

import static io.github.nigalranieri.jsondiffer.support.JsonTestResource.path;
import static org.junit.jupiter.api.Assertions.*;

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

  @Test
  void shouldRejectNullActualPath() {
    Path valid = path("json/identical-expected.json");

    assertThrows(NullPointerException.class, () -> JsonCompare.compare(valid, (Path) null));
  }

  @Test
  void shouldCompareExpectedStringWithActualPath() {
    String expected = "{\"name\":\"Alice\",\"age\":30}";
    Path actual = path("json/identical-actual.json");

    assertTrue(JsonCompare.areEqual(expected, actual));
  }

  @Test
  void shouldCompareExpectedPathWithActualString() {
    Path expected = path("json/identical-expected.json");
    String actual = "{\"age\":30,\"name\":\"Alice\"}";

    assertTrue(JsonCompare.areEqual(expected, actual));
  }

  @Test
  void shouldReportDifferenceBetweenExpectedStringAndActualPath() {
    String expected = "{\"name\":\"Alice\"}";
    Path actual = path("json/different-actual.json");

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
  }

  @Test
  void shouldReportDifferenceBetweenExpectedPathAndActualString() {
    Path expected = path("json/different-expected.json");
    String actual = "{\"name\":\"Bob\"}";

    ComparisonResult result = JsonCompare.compare(expected, actual);

    assertFalse(result.isEqual());
  }

  @Test
  void shouldCompareTwoPathsForEquality() {
    Path expected = path("json/identical-expected.json");
    Path actual = path("json/identical-actual.json");

    assertTrue(JsonCompare.areEqual(expected, actual));
  }
}
