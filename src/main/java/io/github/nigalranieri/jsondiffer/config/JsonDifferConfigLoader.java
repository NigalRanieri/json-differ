package io.github.nigalranieri.jsondiffer.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class JsonDifferConfigLoader {

  private static final ObjectMapper YAML_MAPPER = createMapper();

  private JsonDifferConfigLoader() {}

  public static JsonDifferConfig load(String yaml) throws IOException {
    Objects.requireNonNull(yaml, "yaml");

    JsonDifferConfig config = YAML_MAPPER.readValue(yaml, JsonDifferConfig.class);

    return config == null ? new JsonDifferConfig() : config;
  }

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
