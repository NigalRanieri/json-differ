package io.github.nigalranieri.jsondiffer.internal.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;

public final class JacksonJsonParser {

  private final ObjectMapper objectMapper;

  public JacksonJsonParser() {
    this.objectMapper = new ObjectMapper();
  }

  public JsonNode parse(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException("Invalid JSON", e);
    }
  }
}
