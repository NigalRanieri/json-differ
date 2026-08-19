package io.github.nigalranieri.jsondiffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;

public final class JsonCompare {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonCompare() {
    }

    public static boolean equals(String first, String second) {
        JsonNode firstNode = parse(first);
        JsonNode secondNode = parse(second);

        return firstNode.equals(secondNode);
    }

    private static JsonNode parse(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON", e);
        }
    }
}