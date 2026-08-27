package io.github.nigalranieri.jsondiffer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfigLoader;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.io.IOException;
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
}
