package io.github.nigalranieri.jsondiffer.internal.path;

import java.util.Objects;

public final class PathValidator {

  private PathValidator() {}

  public static void validate(String path) {
    Objects.requireNonNull(path, "path");
    if (!isValid(path)) {
      throw new IllegalArgumentException("Invalid JSON path: " + path);
    }
  }

  private static boolean isValid(String path) {
    if ("$".equals(path)) {
      return true;
    }

    if (!path.startsWith("$.") || path.length() == 2) {
      return false;
    }

    String[] segments = path.substring(2).split("\\.", -1);

    for (String segment : segments) {
      if (!isValidSegment(segment)) {
        return false;
      }
    }

    return true;
  }

  private static boolean isValidSegment(String segment) {
    if (segment.isEmpty()) {
      return false;
    }

    if ("*".equals(segment) || "**".equals(segment)) {
      return true;
    }

    int bracket = segment.indexOf('[');

    if (bracket < 0) {
      return isValidProperty(segment);
    }

    String property = segment.substring(0, bracket);

    if (!isValidProperty(property)) {
      return false;
    }

    return areValidArraySuffixes(segment.substring(bracket));
  }

  private static boolean isValidProperty(String property) {
    return !property.isEmpty()
        && property.indexOf('[') < 0
        && property.indexOf(']') < 0
        && property.indexOf('*') < 0;
  }

  private static boolean areValidArraySuffixes(String suffixes) {
    int position = 0;

    while (position < suffixes.length()) {
      if (suffixes.charAt(position) != '[') {
        return false;
      }

      int closingBracket = suffixes.indexOf(']', position);

      if (closingBracket < 0) {
        return false;
      }

      String index = suffixes.substring(position + 1, closingBracket);

      if (!"*".equals(index) && !isNonNegativeInteger(index)) {
        return false;
      }

      position = closingBracket + 1;
    }

    return true;
  }

  private static boolean isNonNegativeInteger(String value) {
    if (value.isEmpty()) {
      return false;
    }

    for (int i = 0; i < value.length(); i++) {
      if (!Character.isDigit(value.charAt(i))) {
        return false;
      }
    }

    return true;
  }
}
