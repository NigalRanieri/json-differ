package io.github.nigalranieri.jsondiffer.result;

/** Describes the kind of difference detected between the expected and actual JSON documents. */
public enum DifferenceType {

  /** The values at the same JSON path are different. */
  VALUE_MISMATCH,

  /** A field exists in the expected JSON object but is missing from the actual object. */
  MISSING_FIELD,

  /** A field exists in the actual JSON object but is absent from the expected object. */
  UNEXPECTED_FIELD,

  /** An array element exists in the expected JSON array but has no corresponding actual element. */
  MISSING_ELEMENT,

  /** An array element exists in the actual JSON array but has no corresponding expected element. */
  UNEXPECTED_ELEMENT,

  /** The string values at the same JSON path differ only in letter casing. */
  CASE_MISMATCH
}
