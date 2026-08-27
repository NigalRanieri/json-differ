package io.github.nigalranieri.jsondiffer.internal;

public final class PathTolerance {

  private final String path;
  private final double tolerance;

  public PathTolerance(String path, double tolerance) {
    this.path = path;
    this.tolerance = tolerance;
  }

  public String getPath() {
    return path;
  }

  public double getTolerance() {
    return tolerance;
  }
}
