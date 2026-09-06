package io.github.nigalranieri.jsondiffer.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Loads {@link JsonDifferConfig} instances from YAML configuration.
 *
 * <p>Configuration can be loaded either from YAML text or from a YAML file.
 *
 * <p>Unknown configuration properties and malformed YAML are rejected during deserialization.
 */
public final class JsonDifferConfigLoader {

  private static final ObjectMapper YAML_MAPPER = createYamlMapper();
  private static final ObjectMapper JSON_MAPPER = createJsonMapper();

  private JsonDifferConfigLoader() {}

  /**
   * Loads configuration from YAML text.
   *
   * <p>Blank YAML is treated as an empty configuration and therefore uses all default settings.
   *
   * @param yaml the YAML configuration
   * @return the parsed configuration
   * @throws NullPointerException if {@code yaml} is {@code null}
   * @throws IOException if the YAML cannot be parsed or mapped to the configuration model
   */
  public static JsonDifferConfig loadYaml(String yaml) throws IOException {
    Objects.requireNonNull(yaml, "yaml");

    if (yaml.trim().isEmpty()) {
      return new JsonDifferConfig();
    }

    JsonDifferConfig config = YAML_MAPPER.readValue(yaml, JsonDifferConfig.class);

    return config == null ? new JsonDifferConfig() : config;
  }

  /**
   * Loads configuration from a YAML or JSON file.
   *
   * <p>Files ending in {@code .json} are parsed as JSON. All other files are parsed as YAML.
   *
   * @param path the path to the configuration file
   * @return the parsed configuration
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IOException if the file cannot be read or its contents cannot be parsed or mapped
   */
  public static JsonDifferConfig load(Path path) throws IOException {
    Objects.requireNonNull(path, "path");

    String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

    String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);

    if (fileName.endsWith(".json")) {
      return loadJson(content);
    }

    return loadYaml(content);
  }

  /**
   * Loads configuration from JSON text.
   *
   * <p>Blank JSON is treated as an empty configuration and therefore uses all default settings.
   *
   * @param json the JSON configuration
   * @return the parsed configuration
   * @throws NullPointerException if {@code json} is {@code null}
   * @throws IOException if the JSON cannot be parsed or mapped to the configuration model
   */
  public static JsonDifferConfig loadJson(String json) throws IOException {
    Objects.requireNonNull(json, "json");

    if (json.trim().isEmpty()) {
      return new JsonDifferConfig();
    }

    JsonDifferConfig config = JSON_MAPPER.readValue(json, JsonDifferConfig.class);

    return config == null ? new JsonDifferConfig() : config;
  }

  private static ObjectMapper createYamlMapper() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    return mapper;
  }

  private static ObjectMapper createJsonMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    return mapper;
  }
}
