package io.github.nigalranieri.jsondiffer.internal.format;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TableFormatterTest {

  @Test
  void shouldFormatTable() {
    List<String> headers = Arrays.asList("PATH", "TYPE", "EXPECTED", "ACTUAL");

    List<List<String>> rows =
        Arrays.asList(
            Arrays.asList("$.name", "VALUE_MISMATCH", "\"Alice\"", "\"Bob\""),
            Arrays.asList("$.age", "MISSING_FIELD", "30", "<missing>"));

    String expected =
        "+--------+----------------+----------+-----------+"
            + System.lineSeparator()
            + "| PATH   | TYPE           | EXPECTED | ACTUAL    |"
            + System.lineSeparator()
            + "+--------+----------------+----------+-----------+"
            + System.lineSeparator()
            + "| $.name | VALUE_MISMATCH | \"Alice\"  | \"Bob\"     |"
            + System.lineSeparator()
            + "| $.age  | MISSING_FIELD  | 30       | <missing> |"
            + System.lineSeparator()
            + "+--------+----------------+----------+-----------+";

    assertEquals(expected, TableFormatter.format(headers, rows, 40));
  }

  @Test
  void shouldWrapLongCellsAcrossMultipleLines() {
    List<String> headers = Arrays.asList("PATH", "TYPE", "EXPECTED", "ACTUAL");

    List<List<String>> rows =
        Collections.singletonList(
            Arrays.asList(
                "$.very.long.path", "VALUE_MISMATCH", "\"very long expected value\"", "\"Bob\""));

    String expected =
        "+------------+------------+------------+--------+"
            + System.lineSeparator()
            + "| PATH       | TYPE       | EXPECTED   | ACTUAL |"
            + System.lineSeparator()
            + "+------------+------------+------------+--------+"
            + System.lineSeparator()
            + "| $.very.lon | VALUE_MISM | \"very long | \"Bob\"  |"
            + System.lineSeparator()
            + "| g.path     | ATCH       |  expected  |        |"
            + System.lineSeparator()
            + "|            |            | value\"     |        |"
            + System.lineSeparator()
            + "+------------+------------+------------+--------+";

    assertEquals(expected, TableFormatter.format(headers, rows, 10));
  }
}
