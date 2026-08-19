package io.github.nigalranieri.jsondiffer.internal.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import io.github.nigalranieri.jsondiffer.exception.JsonReadException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class JacksonJsonParser {

  private final ObjectMapper objectMapper;

  public JacksonJsonParser() {
    this.objectMapper = new ObjectMapper();
  }

  public JsonNode parse(String json) {
    Objects.requireNonNull(json, "json");
    try {
      JsonNode node = objectMapper.readTree(json);

      if (node == null || node.isMissingNode()) {
        throw new InvalidJsonException("JSON content cannot be empty");
      }

      return node;
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException("Invalid JSON", e);
    }
  }

  public JsonNode parse(Path path) {
    Objects.requireNonNull(path, "path");
    try {
      JsonNode node = objectMapper.readTree(path.toFile());

      if (node == null || node.isMissingNode()) {
        throw new InvalidJsonException("JSON content cannot be empty: " + path);
      }

      return node;
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException("Invalid JSON: " + path, e);
    } catch (IOException e) {
      throw new JsonReadException("Unable to read JSON file: " + path, e);
    }
  }
}
