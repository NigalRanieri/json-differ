package io.github.nigalranieri.jsondiffer.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Loads {@link JsonDifferConfig} instances from YAML configuration.
 *
 * <p>Configuration can be loaded either from YAML text or from a YAML file.
 *
 * <p>Unknown configuration properties and malformed YAML are rejected during deserialization.
 */
public final class JsonDifferConfigLoader {

  private static final ObjectMapper YAML_MAPPER = createMapper();

  private JsonDifferConfigLoader() {}

  /**
   * Loads configuration from YAML text.
   *
   * @param yaml the YAML configuration
   * @return the parsed configuration
   * @throws NullPointerException if {@code yaml} is {@code null}
   * @throws IOException if the YAML cannot be parsed or mapped to the configuration model
   */
  public static JsonDifferConfig load(String yaml) throws IOException {
    Objects.requireNonNull(yaml, "yaml");

    JsonDifferConfig config = YAML_MAPPER.readValue(yaml, JsonDifferConfig.class);

    return config == null ? new JsonDifferConfig() : config;
  }

  /**
   * Loads configuration from a YAML file.
   *
   * @param path the path to the YAML configuration file
   * @return the parsed configuration
   * @throws NullPointerException if {@code path} is {@code null}
   * @throws IOException if the file cannot be read or its contents cannot be parsed or mapped
   */
  public static JsonDifferConfig load(Path path) throws IOException {
    Objects.requireNonNull(path, "path");

    JsonDifferConfig config = YAML_MAPPER.readValue(path.toFile(), JsonDifferConfig.class);

    return config == null ? new JsonDifferConfig() : config;
  }

  private static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
    return mapper;
  }
}
