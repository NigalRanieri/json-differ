# json-differ
[![Build](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml/badge.svg)](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.nigalranieri/json-differ.svg)](https://central.sonatype.com/artifact/io.github.nigalranieri/json-differ)
![Java](https://img.shields.io/badge/Java-8%2B-blue)
![Coverage](https://img.shields.io/badge/Coverage-%E2%89%A590%25-brightgreen)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A configurable structural JSON comparison and diffing library for Java.

`json-differ` compares JSON documents by structure and value and returns a detailed, programmatically accessible description of every difference.

It is strict by default, while allowing comparison behavior to be customized when needed.

## Live Playground

Try **json-differ** directly in your browser — no installation required.

**[Open the json-differ Playground](https://nigalranieri.github.io/json-differ/demo/)**

The playground runs the real Java library in your browser and exposes the full comparison configuration through optional YAML, including path-aware rules and result formatting.

## Features

- Structural JSON comparison
- Detailed differences with JSON paths
- Ordered arrays by default
- Optional global and path-specific unordered array comparison
- Ignored paths with wildcard support
- Optional global and path-specific `null`/missing equivalence
- Numeric comparison with global and path-specific tolerance
- Optional global and path-specific case-insensitive string comparison
- Consistent wildcard support across path-specific comparison rules
- Recursive path matching with `**`
- String and file-based input
- Reusable configured comparators
- Optional YAML configuration from strings or files
- Configurable traversal and grouped result formatting
- Java 8 compatible

## Installation

`json-differ` is available from Maven Central.

### Maven

```xml
<dependency>
    <groupId>io.github.nigalranieri</groupId>
    <artifactId>json-differ</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

**Groovy DSL**

```groovy
dependencies {
    implementation 'io.github.nigalranieri:json-differ:0.1.0'
}
```

**Kotlin DSL**

```kotlin
dependencies {
    implementation("io.github.nigalranieri:json-differ:0.1.0")
}
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

To treat `null` and missing fields as equivalent globally:

```java
ComparisonResult result =
    JsonCompare.builder()
        .treatNullAndMissingAsEqual()
        .compare(expected, actual);
```

The rule can also be enabled only at specific paths:

```java
ComparisonResult result =
    JsonCompare.builder()
        .treatNullAndMissingAsEqual("$.users[*].nickname")
        .compare(expected, actual);
```

Other fields remain strict unless global null/missing equivalence is enabled.

This option applies to object fields and does not change array semantics.

### Numeric tolerance

Numeric values are compared exactly by default.

Configure a global absolute tolerance when approximate numeric comparison is needed:

```java
ComparisonResult result =
    JsonCompare.builder()
        .numericTolerance(0.01)
        .compare(expected, actual);
```

With a tolerance of `0.01`, numeric values whose absolute difference is less than or equal to `0.01` are considered equal.

Different tolerances can also be configured for specific paths:

```java
ComparisonResult result =
    JsonCompare.builder()
        .numericTolerance(0.01)
        .numericTolerance("$.measurements[*].value", 0.1)
        .numericTolerance("$.price", 0.001)
        .compare(expected, actual);
```

A matching path-specific tolerance takes precedence over the global tolerance. If multiple path-specific tolerance rules match the same path, the last configured matching tolerance is used.

When no path-specific tolerance matches, the global tolerance is used. If no global tolerance is configured either, numeric values are compared exactly.

Tolerances must be non-negative and finite.

### Ignore string case

String values are case-sensitive by default.

To compare all string values without considering case:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreCase()
        .compare(expected, actual);
```

For example, `"Alice"` and `"alice"` are considered equal when case-insensitive comparison is enabled.

Case-insensitive comparison can also be enabled only at specific paths:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreCase("$.users[*].email")
        .compare(expected, actual);
```

String values at other paths remain case-sensitive unless global case-insensitive comparison is enabled.

This option applies only to string values. Object field names remain case-sensitive.

### Combine options

Comparison rules can be combined:

```java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.metadata.timestamp")
        .ignoreArrayOrder("$.users")
        .treatNullAndMissingAsEqual("$.users[*].nickname")
        .numericTolerance("$.users[*].score", 0.01)
        .ignoreCase("$.users[*].email")
        .compare(expected, actual);
```

Ignored paths take precedence over other comparison rules at the same path.

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

## YAML Configuration

The builder API remains fully supported, but comparison and output behavior can also be described in YAML.

YAML configuration is optional. Blank YAML, omitted sections, and explicitly `null` sections fall back to the default strict comparison behavior.

### Load configuration from a YAML file

Use `JsonCompare.fromConfig(Path)` when configuration is stored in a file:

```java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.JsonComparator;
import java.nio.file.Paths;

JsonComparator comparator =
    JsonCompare.fromConfig(
        Paths.get("json-differ.yml"));

ComparisonResult result =
    comparator.compare(expected, actual);
```

### Load configuration from YAML text

YAML text can be parsed into a reusable `JsonDifferConfig`:

```java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.JsonComparator;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfigLoader;

String yaml =
    "comparison:\n"
        + "  ignoreCase:\n"
        + "    globally: true\n";

JsonDifferConfig config =
    JsonDifferConfigLoader.load(yaml);

JsonComparator comparator =
    JsonCompare.fromConfig(config);

ComparisonResult result =
    comparator.compare(expected, actual);
```

The same configuration object also contains output settings:

```java
String formatted =
    config.getOutput().format(result);
```

### Complete YAML example

```yaml
comparison:
  ignorePaths:
    - $.metadata.requestId
    - $.metadata.timestamp

  arrayOrder:
    ignoreGlobally: false
    ignoreAt:
      - $.roles
      - $.groups[*].members

  nullAndMissing:
    equalGlobally: false
    equalAt:
      - $.user.nickname
      - $.users[*].optional

  numericTolerance:
    global: 0.01
    paths:
      $.user.score: 0.5
      $.measurements[*].value: 0.1

  ignoreCase:
    globally: false
    paths:
      - $.user.email
      - $.users[*].username

output:
  format: grouped
  columns:
    maxCellWidth: 40
```

All settings are optional. The default configuration is equivalent to strict comparison with traversal output and a maximum table cell width of `40`.

### YAML comparison options

| Option | Default | Description |
| --- | --- | --- |
| `comparison.ignorePaths` | empty | Paths or path patterns whose values or subtrees are ignored. |
| `comparison.arrayOrder.ignoreGlobally` | `false` | Ignores element order for every array. |
| `comparison.arrayOrder.ignoreAt` | empty | Ignores element order only for arrays matching the listed paths. |
| `comparison.nullAndMissing.equalGlobally` | `false` | Treats JSON `null` and a missing object field as equal globally. |
| `comparison.nullAndMissing.equalAt` | empty | Enables null/missing equivalence only at matching paths. |
| `comparison.numericTolerance.global` | none | Sets the global absolute numeric tolerance. |
| `comparison.numericTolerance.paths` | empty | Sets path-specific numeric tolerances. |
| `comparison.ignoreCase.globally` | `false` | Compares all string values without considering case. |
| `comparison.ignoreCase.paths` | empty | Enables case-insensitive string comparison only at matching paths. |

Path-specific YAML rules use the same path syntax and wildcard semantics as the builder API.

For numeric tolerance, a matching path-specific tolerance overrides the global tolerance. If multiple path-specific tolerance rules match the same path, the last configured matching tolerance is used.

Global boolean rules remain enabled everywhere when set to `true`; path-specific rules add matching paths rather than disabling a global rule.

### YAML output options

| Option | Default | Description |
| --- | --- | --- |
| `output.format` | `traversal` | Result presentation mode. Accepted values are `traversal` and `grouped`, case-insensitively. |
| `output.columns.maxCellWidth` | `40` | Maximum width of each formatted table cell before wrapping. Must be greater than zero. |

For example:

```yaml
output:
  format: grouped
  columns:
    maxCellWidth: 60
```

Output configuration affects rendering only. It does not change the structured differences returned by `ComparisonResult`.

### Validation

Configuration is validated rather than silently ignored.

- Unknown YAML properties are rejected.
- Malformed YAML is rejected.
- Paths must use valid json-differ path syntax and start with `$`.
- Numeric tolerances must be non-negative and finite.
- `maxCellWidth` must be greater than zero.
- Blank YAML is treated as an empty configuration.

## Path Syntax

All path-specific comparison rules use the same path syntax. Path expressions start from `$`, the document root.

This applies to:

- `ignorePath(...)`
- `ignoreArrayOrder(...)`
- `treatNullAndMissingAsEqual(...)`
- `numericTolerance(...)`
- `ignoreCase(...)`

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

// Treat null and missing nicknames as equivalent
.treatNullAndMissingAsEqual("$.users[*].nickname")

// Use a different tolerance for measurement values
.numericTolerance("$.measurements[*].value", 0.1)

// Compare email values without considering case
.ignoreCase("$.users[*].email")
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
- String values are case-sensitive.
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

A custom maximum cell width can also be supplied directly:

```java
System.out.println(
    result.format(
        ComparisonResultFormat.TRAVERSAL,
        60));
```

The default maximum cell width is `40`.

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
