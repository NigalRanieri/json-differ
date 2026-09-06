package io.github.nigalranieri.jsondiffer.internal.path;

public final class PathMatcher {

  public boolean matches(String pattern, String path) {
    String[] patternTokens = tokenize(pattern);
    String[] pathTokens = tokenize(path);

    return matches(patternTokens, 0, pathTokens, 0);
  }

  private boolean matches(
      String[] patternTokens, int patternIndex, String[] pathTokens, int pathIndex) {

    if (patternIndex == patternTokens.length) {
      return pathIndex == pathTokens.length;
    }

    String patternToken = patternTokens[patternIndex];

    if ("**".equals(patternToken)) {
      // ** matches zero tokens
      if (matches(patternTokens, patternIndex + 1, pathTokens, pathIndex)) {
        return true;
      }

      // ** matches one or more tokens
      return pathIndex < pathTokens.length
          && matches(patternTokens, patternIndex, pathTokens, pathIndex + 1);
    }

    if (pathIndex >= pathTokens.length) {
      return false;
    }

    String pathToken = pathTokens[pathIndex];

    if ("*".equals(patternToken)) {
      return matches(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
    }

    if ("[*]".equals(patternToken) && isArrayIndex(pathToken)) {
      return matches(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
    }

    if (!patternToken.equals(pathToken)) {
      return false;
    }

    return matches(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
  }

  private boolean matchesOrIsAncestor(
      String[] patternTokens, int patternIndex, String[] pathTokens, int pathIndex) {

    if (patternIndex == patternTokens.length) {
      return true;
    }

    if (pathIndex == pathTokens.length) {
      return true;
    }

    String patternToken = patternTokens[patternIndex];
    String pathToken = pathTokens[pathIndex];

    if ("**".equals(patternToken)) {
      if (matchesOrIsAncestor(patternTokens, patternIndex + 1, pathTokens, pathIndex)) {
        return true;
      }

      return matchesOrIsAncestor(patternTokens, patternIndex, pathTokens, pathIndex + 1);
    }

    if ("*".equals(patternToken)) {
      return matchesOrIsAncestor(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
    }

    if ("[*]".equals(patternToken) && isArrayIndex(pathToken)) {
      return matchesOrIsAncestor(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
    }

    if (!patternToken.equals(pathToken)) {
      return false;
    }

    return matchesOrIsAncestor(patternTokens, patternIndex + 1, pathTokens, pathIndex + 1);
  }

  public boolean matchesOrIsAncestor(String pattern, String path) {
    if ("$".equals(path)) {
      return true;
    }

    String[] patternTokens = tokenize(pattern);
    String[] pathTokens = tokenize(path);

    return matchesOrIsAncestor(patternTokens, 0, pathTokens, 0);
  }

  public boolean matchesOrIsDescendant(String pattern, String path) {
    String[] pathTokens = tokenize(path);

    for (int length = pathTokens.length; length > 0; length--) {
      String candidate = buildPath(pathTokens, length);

      if (matches(pattern, candidate)) {
        return true;
      }
    }

    return false;
  }

  private String buildPath(String[] tokens, int length) {
    StringBuilder path = new StringBuilder("$");

    for (int i = 0; i < length; i++) {
      String token = tokens[i];

      if (token.startsWith("[")) {
        path.append(token);
      } else {
        path.append(".").append(token);
      }
    }

    return path.toString();
  }

  private String[] tokenize(String path) {
    return path.replace("$.", "").replaceAll("(\\[\\d+\\]|\\[\\*\\])", ".$1").split("\\.");
  }

  private boolean isArrayIndex(String token) {
    return token.matches("\\[\\d+\\]");
  }
}
