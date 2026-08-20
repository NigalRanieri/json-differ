# json-differ

[![Build](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml/badge.svg)](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.nigalranieri/json-differ.svg)](https://central.sonatype.com/artifact/io.github.nigalranieri/json-differ)
![Java](https://img.shields.io/badge/Java-8%2B-blue)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A configurable structural JSON comparison and diffing library for Java.

`json-differ` compares JSON documents by structure and value and returns a detailed, programmatically accessible description of every difference.

It is strict by default, while allowing comparison behavior to be customized when needed.

## Features

- Structural JSON comparison
- Detailed differences with JSON paths
- Ordered arrays by default
- Optional unordered array comparison
- Path-specific unordered arrays
- Ignored paths with wildcard support
- Recursive path matching with `**`
- Optional `null` and missing-field equivalence
- Numeric comparison with configurable tolerance
- String and file-based input
- Reusable configured comparators
- Traversal and grouped result formatting
- Java 8 compatible

## Installation

`json-differ` is available from Maven Central.

Add the following dependency to your Maven project:

```xml
<dependency>
    <groupId>io.github.nigalranieri</groupId>
    <artifactId>json-differ</artifactId>
    <version>0.1.0</version>
</dependency>
```

`json-differ` requires Java 8 or later.

## Quick Start

Compare two JSON documents:

```java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

String expected =
    "{\"name\":\"Alice\",\"age\":30}";

String actual =
    "{\"name\":\"Bob\",\"age\":30}";

ComparisonResult result =
    JsonCompare.compare(expected, actual);

if (!result.isEqual()) {
  System.out.println(result);
}
```

Output:

```text
JSON differs (1 differences):

+--------+----------------+----------+--------+
| PATH   | TYPE           | EXPECTED | ACTUAL |
+--------+----------------+----------+--------+
| $.name | VALUE_MISMATCH | "Alice"  | "Bob"  |
+--------+----------------+----------+--------+
```

For a simple equality check:

```java
boolean equal =
    JsonCompare.equals(expected, actual);
```

## Configuration

Use `JsonCompare.builder()` to customize comparison behavior.

### Ignore paths

Ignore differences at a specific JSON path:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.timestamp")
        .compare(expected, actual);
```

Ignoring a path ignores the value or subtree at that path.

Wildcards are supported:

```java
.ignorePath("$.users[*].timestamp")
```

Recursive wildcards can match at any nested depth:

```java
.ignorePath("$.**.timestamp")
```

### Ignore array order

Arrays are order-sensitive by default.

To ignore order for all arrays:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreArrayOrder()
        .compare(expected, actual);
```

For example, these arrays are considered equal when array order is ignored:

```json
[1, 2, 3]
```

```json
[3, 1, 2]
```

Duplicate elements remain significant.

Array order can also be ignored only at specific paths:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreArrayOrder("$.users")
        .compare(expected, actual);
```

Path wildcards are supported here as well:

```java
.ignoreArrayOrder("$.groups[*].users")
```

### Treat `null` and missing fields as equal

By default, an explicit JSON `null` and a missing object field are different.

For example:

```json
{
  "name": null
}
```

and:

```json
{}
```

are different under the default comparison rules.

To treat them as equivalent:

```java
ComparisonResult result =
    JsonCompare.builder()
        .treatNullAndMissingAsEqual()
        .compare(expected, actual);
```

This option applies to object fields and does not change array semantics.

### Numeric tolerance

Numeric values are compared exactly by default.

Configure an absolute tolerance when approximate numeric comparison is needed:

```java
ComparisonResult result =
    JsonCompare.builder()
        .numericTolerance(0.01)
        .compare(expected, actual);
```

With a tolerance of `0.01`, numeric values whose absolute difference is less than or equal to `0.01` are considered equal.

The tolerance must be non-negative and finite.

### Combine options

Options can be combined:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.timestamp")
        .ignoreArrayOrder("$.users")
        .treatNullAndMissingAsEqual()
        .numericTolerance(0.001)
        .compare(expected, actual);
```

### Reusable comparators

For repeated comparisons with the same configuration, build a reusable comparator:

```java
JsonComparator comparator =
    JsonCompare.builder()
        .ignorePath("$.timestamp")
        .numericTolerance(0.01)
        .build();

ComparisonResult firstResult =
    comparator.compare(firstExpected, firstActual);

ComparisonResult secondResult =
    comparator.compare(secondExpected, secondActual);
```

A built comparator keeps the configuration it was created with and is unaffected by subsequent changes to the builder.

## Path Syntax

Configuration methods such as `ignorePath(...)` and path-specific `ignoreArrayOrder(...)` use path expressions starting from `$`, the document root.

| Syntax | Meaning | Example |
| --- | --- | --- |
| `$` | Document root | `$` |
| `.property` | Object property | `$.user.name` |
| `[n]` | Exact array index | `$.users[0]` |
| `*` | Any property at one level | `$.*.timestamp` |
| `[*]` | Any array index | `$.users[*].id` |
| `**` | Recursive wildcard across nested levels | `$.**.timestamp` |

Examples:

```java
// Ignore one exact property
.ignorePath("$.metadata.timestamp")

// Ignore the timestamp of every user
.ignorePath("$.users[*].timestamp")

// Ignore timestamp wherever it appears recursively
.ignorePath("$.**.timestamp")

// Ignore array order for every users array inside groups
.ignoreArrayOrder("$.groups[*].users")
```

Paths must be syntactically valid and start with `$`. Invalid paths are rejected with `IllegalArgumentException`.

## Default Comparison Semantics

`json-differ` performs strict structural comparison by default.

Without additional configuration:

- Object property order is ignored.
- Object fields must exist on both sides.
- Array order is significant.
- Array length and duplicate elements are significant.
- JSON `null` and a missing field are different.
- Numbers are compared exactly.
- Values of different JSON types are not considered equal.
- No paths are ignored.

For example, object property order does not affect equality:

```json
{
  "name": "Alice",
  "age": 30
}
```

and:

```json
{
  "age": 30,
  "name": "Alice"
}
```

are equal.

Array order does affect equality:

```json
[1, 2, 3]
```

and:

```json
[3, 2, 1]
```

are different unless unordered array comparison is enabled.

## Working with Differences

`JsonCompare.compare(...)` returns a `ComparisonResult`.

```java
ComparisonResult result =
    JsonCompare.compare(expected, actual);

if (!result.isEqual()) {
  for (Difference difference : result.getDifferences()) {
    System.out.println(difference.getPath());
    System.out.println(difference.getType());
    System.out.println(difference.getExpected());
    System.out.println(difference.getActual());
  }
}
```

Each `Difference` contains:

- the JSON path where the difference was detected
- the `DifferenceType`
- the expected `DifferenceValue`
- the actual `DifferenceValue`

Example:

```java
Difference difference =
    result.getDifferences().get(0);

String path = difference.getPath();
DifferenceType type = difference.getType();

DifferenceValue expectedValue =
    difference.getExpected();

DifferenceValue actualValue =
    difference.getActual();
```

### Difference types

Possible difference types are:

- `VALUE_MISMATCH`
- `MISSING_FIELD`
- `UNEXPECTED_FIELD`
- `MISSING_ELEMENT`
- `UNEXPECTED_ELEMENT`

### Difference values

`DifferenceValue` preserves the JSON value type.

```java
DifferenceValueType type =
    difference.getExpected().getType();
```

Supported value types are:

- `STRING`
- `NUMBER`
- `BOOLEAN`
- `OBJECT`
- `ARRAY`
- `NULL`
- `MISSING`

JSON objects are exposed as immutable `Map` values and arrays as immutable `List` values. Jackson types are not exposed through the public result API.

Missing values and explicit JSON `null` values remain distinct:

```java
difference.getExpected().isMissing();
difference.getExpected().isNull();
```

For example:

```json
{
  "age": null
}
```

and:

```json
{}
```

can produce a result where one side is `NULL` and the other is `MISSING`.

## Result Formatting

`ComparisonResult` provides human-readable table formatting in addition to the structured difference API.

### Traversal format

Traversal format is the default used by `toString()`:

```java
System.out.println(result);
```

or explicitly:

```java
System.out.println(
    result.format(ComparisonResultFormat.TRAVERSAL));
```

Differences are displayed in traversal order, with the JSON path as the first column:

```text
JSON differs (3 differences):

+----------+------------------+-----------+-----------+
| PATH     | TYPE             | EXPECTED  | ACTUAL    |
+----------+------------------+-----------+-----------+
| $.name   | VALUE_MISMATCH   | "Alice"   | "Bob"     |
| $.age    | MISSING_FIELD    | 30        | <missing> |
| $.active | UNEXPECTED_FIELD | <missing> | true      |
+----------+------------------+-----------+-----------+
```

### Grouped format

Grouped format organizes differences by type:

```java
System.out.println(
    result.format(ComparisonResultFormat.GROUPED));
```

The difference type becomes the first column:

```text
JSON differs (4 differences):

+------------------+----------+-----------+-----------+
| TYPE             | PATH     | EXPECTED  | ACTUAL    |
+------------------+----------+-----------+-----------+
| VALUE_MISMATCH   | $.name   | "Alice"   | "Bob"     |
| VALUE_MISMATCH   | $.city   | "Rome"    | "Milan"   |
| MISSING_FIELD    | $.age    | 30        | <missing> |
| UNEXPECTED_FIELD | $.active | <missing> | true      |
+------------------+----------+-----------+-----------+
```

Grouped formatting changes only the presentation of the result. `getDifferences()` continues to return differences in their original traversal order.

Long paths and values are wrapped across multiple table lines rather than truncated.

## File Comparison

JSON files can be compared directly using `Path`:

```java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import java.nio.file.Path;
import java.nio.file.Paths;

Path expected =
    Paths.get("expected.json");

Path actual =
    Paths.get("actual.json");

ComparisonResult result =
    JsonCompare.compare(expected, actual);
```

Builder options work with file comparison as well:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.timestamp")
        .ignoreArrayOrder("$.users")
        .compare(expected, actual);
```

Malformed JSON is reported with `InvalidJsonException`. Failures while reading a JSON file are reported with `JsonReadException`.

## Requirements

- Java 8 or later

Jackson is used internally for JSON parsing but is not exposed through the public comparison or result APIs.

## License

Copyright © 2026 Nigal Ranieri.

Licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.