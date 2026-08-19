package io.github.nigalranieri.jsondiffer.result;

/** Describes the JSON value type represented by a {@link DifferenceValue}. */
public enum DifferenceValueType {

  /** Represents an absent field or array element. */
  MISSING,

  /** Represents an explicit JSON {@code null}. */
  NULL,

  /** Represents a JSON string. */
  STRING,

  /** Represents a JSON number. */
  NUMBER,

  /** Represents a JSON boolean. */
  BOOLEAN,

  /** Represents a JSON object, exposed as an immutable Java {@link java.util.Map}. */
  OBJECT,

  /** Represents a JSON array, exposed as an immutable Java {@link java.util.List}. */
  ARRAY
}
