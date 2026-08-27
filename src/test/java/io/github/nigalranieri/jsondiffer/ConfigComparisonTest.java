package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfigLoader;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class ConfigComparisonTest {

  @Test
  void appliesYamlComparisonConfiguration() throws IOException {
    String yaml =
        "comparison:\n"
            + "  ignorePaths:\n"
            + "    - $.timestamp\n"
            + "  arrayOrder:\n"
            + "    ignoreAt:\n"
            + "      - $.users\n"
            + "  nullAndMissing:\n"
            + "    equalAt:\n"
            + "      - $.users[*].nickname\n"
            + "  numericTolerance:\n"
            + "    paths:\n"
            + "      $.users[*].score: 0.1\n"
            + "  ignoreCase:\n"
            + "    paths:\n"
            + "      - $.users[*].email\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    ComparisonResult result =
        JsonCompare.fromConfig(config)
            .compare(
                "{\"timestamp\":1,\"users\":["
                    + "{\"id\":1,\"email\":\"A@EXAMPLE.COM\",\"score\":10.0,\"nickname\":null},"
                    + "{\"id\":2,\"email\":\"b@example.com\",\"score\":20.0}"
                    + "]}",
                "{\"timestamp\":999,\"users\":["
                    + "{\"id\":2,\"email\":\"B@EXAMPLE.COM\",\"score\":20.05},"
                    + "{\"id\":1,\"email\":\"a@example.com\",\"score\":10.05}"
                    + "]}");

    assertTrue(result.isEqual());
  }

  @Test
  void appliesYamlOutputConfiguration() throws IOException {
    String yaml = "output:\n" + "  format: grouped\n" + "  columns:\n" + "    maxCellWidth: 10\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    ComparisonResult result =
        JsonCompare.fromConfig(config)
            .compare(
                "{\"veryLongPropertyName\":\"expected value\"}",
                "{\"veryLongPropertyName\":\"actual value\"}");

    String formatted =
        result.format(
            config.getOutput().getFormat(), config.getOutput().getColumns().getMaxCellWidth());

    assertTrue(formatted.contains("| TYPE"));
    assertTrue(formatted.contains("| PATH"));
  }

  @Test
  void loadsComparisonConfigurationDirectlyFromPath() throws IOException {
    Path configPath = Files.createTempFile("json-differ-", ".yml");

    try {
      Files.write(
          configPath,
          Arrays.asList("comparison:", "  ignoreCase:", "    globally: true"),
          StandardCharsets.UTF_8);

      ComparisonResult result =
          JsonCompare.fromConfig(configPath)
              .compare("{\"name\":\"Alice\"}", "{\"name\":\"alice\"}");

      assertTrue(result.isEqual());
    } finally {
      Files.deleteIfExists(configPath);
    }
  }

  @Test
  void rejectsNullPathEntryFromConfiguration() throws IOException {
    String yaml = "comparison:\n" + "  ignorePaths:\n" + "    - null\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertThrows(NullPointerException.class, () -> JsonCompare.fromConfig(config));
  }

  @Test
  void rejectsInvalidPathFromConfiguration() throws IOException {
    String yaml = "comparison:\n" + "  ignoreCase:\n" + "    paths:\n" + "      - users[*].email\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertThrows(IllegalArgumentException.class, () -> JsonCompare.fromConfig(config));
  }

  @Test
  void rejectsNegativeNumericToleranceFromConfiguration() throws IOException {
    String yaml = "comparison:\n" + "  numericTolerance:\n" + "    global: -0.1\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertThrows(IllegalArgumentException.class, () -> JsonCompare.fromConfig(config));
  }

  @Test
  void invalidOutputConfigurationCannotBeLoadedForComparison() {
    String yaml = "output:\n" + "  columns:\n" + "    maxCellWidth: 0\n";

    assertThrows(IOException.class, () -> JsonDifferConfigLoader.load(yaml));
  }
}
