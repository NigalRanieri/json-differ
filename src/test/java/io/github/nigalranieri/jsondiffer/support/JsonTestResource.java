package io.github.nigalranieri.jsondiffer.support;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class JsonTestResource {

  private JsonTestResource() {}

  public static Path path(String resource) {
    URL url =
        Objects.requireNonNull(
            JsonTestResource.class.getClassLoader().getResource(resource),
            "Test resource not found: " + resource);

    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Invalid test resource URI: " + resource, e);
    }
  }

  public static String load(String resource) {
    Path path = path(resource);

    try {
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read test resource: " + resource, e);
    }
  }
}
