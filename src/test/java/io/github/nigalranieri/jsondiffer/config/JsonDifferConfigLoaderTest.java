package io.github.nigalranieri.jsondiffer.config;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class JsonDifferConfigLoaderTest {
  @Test
  void loadsCompleteYamlConfiguration() throws IOException {
    String yaml =
        "comparison:\n"
            + "  ignorePaths:\n"
            + "    - $.metadata.timestamp\n"
            + "  arrayOrder:\n"
            + "    ignoreGlobally: true\n"
            + "    ignoreAt:\n"
            + "      - $.users\n"
            + "  nullAndMissing:\n"
            + "    equalGlobally: true\n"
            + "    equalAt:\n"
            + "      - $.optional.*\n"
            + "  numericTolerance:\n"
            + "    global: 0.01\n"
            + "    paths:\n"
            + "      $.price: 0.1\n"
            + "  ignoreCase:\n"
            + "    globally: true\n"
            + "    paths:\n"
            + "      - $.users[*].email\n"
            + "output:\n"
            + "  format: grouped\n"
            + "  columns:\n"
            + "    maxCellWidth: 50\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertEquals(
        Collections.singletonList("$.metadata.timestamp"), config.getComparison().getIgnorePaths());

    assertTrue(config.getComparison().getArrayOrder().isIgnoreGlobally());
    assertEquals(
        Collections.singletonList("$.users"), config.getComparison().getArrayOrder().getIgnoreAt());

    assertTrue(config.getComparison().getNullAndMissing().isEqualGlobally());

    assertEquals(Double.valueOf(0.01), config.getComparison().getNumericTolerance().getGlobal());

    assertEquals(
        Double.valueOf(0.1),
        config.getComparison().getNumericTolerance().getPaths().get("$.price"));

    assertTrue(config.getComparison().getIgnoreCase().isGlobally());

    assertEquals(ComparisonResultFormat.GROUPED, config.getOutput().getFormat());

    assertEquals(Integer.valueOf(50), config.getOutput().getColumns().getMaxCellWidth());
  }

  @Test
  void usesEmptyConfigurationWhenSectionsAreOmitted() throws IOException {
    JsonDifferConfig config = JsonDifferConfigLoader.load("{}");

    assertNotNull(config);
    assertNotNull(config.getComparison());
    assertNotNull(config.getOutput());

    assertTrue(config.getComparison().getIgnorePaths().isEmpty());
    assertFalse(config.getComparison().getArrayOrder().isIgnoreGlobally());
    assertTrue(config.getComparison().getArrayOrder().getIgnoreAt().isEmpty());
    assertFalse(config.getComparison().getNullAndMissing().isEqualGlobally());
    assertNull(config.getComparison().getNumericTolerance().getGlobal());
    assertFalse(config.getComparison().getIgnoreCase().isGlobally());

    assertEquals(ComparisonResultFormat.TRAVERSAL, config.getOutput().getFormat());

    assertEquals(Integer.valueOf(40), config.getOutput().getColumns().getMaxCellWidth());
  }

  @Test
  void preservesDefaultsWhenNestedConfigurationIsPartial() throws IOException {
    String yaml = "comparison:\n" + "  ignoreCase:\n" + "    globally: true\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertTrue(config.getComparison().getIgnoreCase().isGlobally());

    assertTrue(config.getComparison().getIgnorePaths().isEmpty());
    assertFalse(config.getComparison().getArrayOrder().isIgnoreGlobally());
    assertTrue(config.getComparison().getArrayOrder().getIgnoreAt().isEmpty());
    assertFalse(config.getComparison().getNullAndMissing().isEqualGlobally());
    assertTrue(config.getComparison().getNullAndMissing().getEqualAt().isEmpty());
    assertNull(config.getComparison().getNumericTolerance().getGlobal());
    assertTrue(config.getComparison().getNumericTolerance().getPaths().isEmpty());

    assertEquals(ComparisonResultFormat.TRAVERSAL, config.getOutput().getFormat());
    assertEquals(Integer.valueOf(40), config.getOutput().getColumns().getMaxCellWidth());
  }

  @Test
  void replacesExplicitNullSectionsWithDefaultConfiguration() throws IOException {
    String yaml =
        "comparison:\n"
            + "  arrayOrder: null\n"
            + "  nullAndMissing: null\n"
            + "  numericTolerance: null\n"
            + "  ignoreCase: null\n"
            + "output: null\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertNotNull(config.getComparison().getArrayOrder());
    assertNotNull(config.getComparison().getNullAndMissing());
    assertNotNull(config.getComparison().getNumericTolerance());
    assertNotNull(config.getComparison().getIgnoreCase());
    assertNotNull(config.getOutput());
  }

  @Test
  void replacesExplicitNullCollectionsWithEmptyCollections() throws IOException {
    String yaml =
        "comparison:\n"
            + "  ignorePaths: null\n"
            + "  arrayOrder:\n"
            + "    ignoreAt: null\n"
            + "  nullAndMissing:\n"
            + "    equalAt: null\n"
            + "  numericTolerance:\n"
            + "    paths: null\n"
            + "  ignoreCase:\n"
            + "    paths: null\n";

    JsonDifferConfig config = JsonDifferConfigLoader.load(yaml);

    assertNotNull(config.getComparison().getIgnorePaths());
    assertNotNull(config.getComparison().getArrayOrder().getIgnoreAt());
    assertNotNull(config.getComparison().getNullAndMissing().getEqualAt());
    assertNotNull(config.getComparison().getNumericTolerance().getPaths());
    assertNotNull(config.getComparison().getIgnoreCase().getPaths());

    assertTrue(config.getComparison().getIgnorePaths().isEmpty());
    assertTrue(config.getComparison().getArrayOrder().getIgnoreAt().isEmpty());
    assertTrue(config.getComparison().getNullAndMissing().getEqualAt().isEmpty());
    assertTrue(config.getComparison().getNumericTolerance().getPaths().isEmpty());
    assertTrue(config.getComparison().getIgnoreCase().getPaths().isEmpty());
  }

  @Test
  void rejectsUnknownConfigurationProperties() {
    String yaml = "comparison:\n" + "  ignoreCase:\n" + "    globaly: true\n";

    assertThrows(IOException.class, () -> JsonDifferConfigLoader.load(yaml));
  }

  @Test
  void rejectsMalformedYaml() {
    String yaml =
        "comparison:\n"
            + "  ignorePaths:\n"
            + "    - $.timestamp\n"
            + "   invalid-indentation: true\n";

    assertThrows(IOException.class, () -> JsonDifferConfigLoader.load(yaml));
  }

  @Test
  void rejectsZeroMaximumCellWidth() {
    String yaml = "output:\n" + "  columns:\n" + "    maxCellWidth: 0\n";

    assertThrows(IOException.class, () -> JsonDifferConfigLoader.load(yaml));
  }

  @Test
  void rejectsNegativeMaximumCellWidth() {
    String yaml = "output:\n" + "  columns:\n" + "    maxCellWidth: -10\n";

    assertThrows(IOException.class, () -> JsonDifferConfigLoader.load(yaml));
  }
}
