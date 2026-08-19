package io.github.nigalranieri.jsondiffer.exception;

/**
 * Thrown when JSON input is syntactically invalid or contains no JSON value.
 *
 * <p>This exception represents invalid JSON content rather than an input-source failure. For
 * example, malformed or empty JSON will cause this exception, while a file that cannot be read is
 * reported through {@link JsonReadException}.
 */
public class InvalidJsonException extends RuntimeException {

  /**
   * Creates an exception with the supplied message.
   *
   * @param message the detail message
   */
  public InvalidJsonException(String message) {
    super(message);
  }

  /**
   * Creates an exception with the supplied message and underlying cause.
   *
   * @param message the detail message
   * @param cause the underlying parsing failure
   */
  public InvalidJsonException(String message, Throwable cause) {
    super(message, cause);
  }
}
