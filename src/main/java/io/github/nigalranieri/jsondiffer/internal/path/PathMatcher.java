package io.github.nigalranieri.jsondiffer.internal.path;

public final class PathMatcher {

  public boolean matches(String pattern, String path) {
    String[] patternTokens = tokenize(pattern);
    String[] pathTokens = tokenize(path);

    if (patternTokens.length != pathTokens.length) {
      return false;
    }

    for (int i = 0; i < patternTokens.length; i++) {
      String patternToken = patternTokens[i];
      String pathToken = pathTokens[i];

      if ("*".equals(patternToken)) {
        continue;
      }

      if ("[*]".equals(patternToken) && isArrayIndex(pathToken)) {
        continue;
      }

      if (!patternToken.equals(pathToken)) {
        return false;
      }
    }

    return true;
  }

  private String[] tokenize(String path) {
    return path.replace("$.", "").replaceAll("(\\[\\d+\\]|\\[\\*\\])", ".$1").split("\\.");
  }

  private boolean isArrayIndex(String token) {
    return token.matches("\\[\\d+\\]");
  }
}
