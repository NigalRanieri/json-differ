package io.github.nigalranieri.jsondiffer.exception;

public class InvalidJsonException extends RuntimeException {

    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}