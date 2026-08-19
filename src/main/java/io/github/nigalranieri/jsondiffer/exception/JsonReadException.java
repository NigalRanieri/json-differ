package io.github.nigalranieri.jsondiffer.exception;

/**
 * Thrown when JSON input cannot be read from its source.
 *
 * <p>This exception represents an input or I/O failure, such as a missing or unreadable file.
 * Successfully read input that contains malformed JSON is reported through {@link
 * InvalidJsonException} instead.
 */
public class JsonReadException extends RuntimeException {

  /**
   * Creates an exception with the supplied message and underlying cause.
   *
   * @param message the detail message
   * @param cause the underlying read failure
   */
  public JsonReadException(String message, Throwable cause) {
    super(message, cause);
  }
}
