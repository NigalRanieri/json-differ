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

  private String[] tokenize(String path) {
    return path.replace("$.", "").replaceAll("(\\[\\d+\\]|\\[\\*\\])", ".$1").split("\\.");
  }

  private boolean isArrayIndex(String token) {
    return token.matches("\\[\\d+\\]");
  }
}
