# json-differ

[![Build](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml/badge.svg)](https://github.com/NigalRanieri/json-differ/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.nigalranieri/json-differ.svg)](https://central.sonatype.com/artifact/io.github.nigalranieri/json-differ)
![Java](https://img.shields.io/badge/Java-8%2B-blue)
![Coverage](https://img.shields.io/badge/Coverage-%E2%89%A590%25-brightgreen)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A configurable structural JSON comparison and diffing library for Java.

`json-differ` compares JSON documents by structure and value and returns
detailed, programmatically accessible differences. Comparison is strict
by default, while allowing behavior to be customized globally or at
specific JSON paths.

## Live Playground

Try **json-differ** directly in your browser --- no installation
required.

**[Open the json-differ Playground](https://nigalranieri.github.io/json-differ/demo/)**

The playground runs the real Java library in the browser and exposes
comparison, result filtering, and output configuration through JSON.

> The Java library supports both JSON and YAML configuration files. The
> playground uses JSON for convenience.

## Contents

-   [Features](#features)
-   [Installation](#installation)
-   [Quick Start](#quick-start)
-   [Configuration](#configuration)
    -   [Ignore paths](#ignore-paths)
    -   [Include paths](#include-paths)
    -   [Ignore array order](#ignore-array-order)
    -   [Treat null and missing fields as
        equal](#treat-null-and-missing-fields-as-equal)
    -   [Numeric tolerance](#numeric-tolerance)
    -   [Ignore string case](#ignore-string-case)
    -   [Combine options](#combine-options)
    -   [Reusable comparators](#reusable-comparators)
-   [JSON and YAML Configuration](#json-and-yaml-configuration)
    -   [Configuration lifecycle](#configuration-lifecycle)
    -   [Loading configuration](#loading-configuration)
    -   [JSON example](#json-example)
    -   [YAML example](#yaml-example)
    -   [Comparison options](#comparison-options)
    -   [Result options](#result-options)
    -   [Output options](#output-options)
    -   [Validation](#validation)
-   [Path Syntax](#path-syntax)
-   [Default Comparison Semantics](#default-comparison-semantics)
-   [Working with Differences](#working-with-differences)
-   [Filtering Results](#filtering-results)
-   [Result Formatting](#result-formatting)
-   [File Comparison](#file-comparison)
-   [Requirements](#requirements)
-   [License](#license)

## Features

-   Structural JSON comparison with detailed JSON paths
-   Ordered arrays by default
-   Optional global and path-specific unordered array comparison
-   Ignored paths with wildcard and recursive wildcard support
-   Included paths for restricting comparison scope
-   Optional global and path-specific `null`/missing equivalence
-   Global and path-specific numeric tolerance
-   Optional global and path-specific case-insensitive string comparison
-   Dedicated `CASE_MISMATCH` classification
-   Result filtering by difference type
-   Regex filtering for string `VALUE_MISMATCH` differences
-   JSON and YAML configuration
-   Configurable traversal and grouped result formatting
-   Custom expected/actual output column labels
-   String and file-based comparison
-   Reusable configured comparators
-   Java 8 compatible

## Installation

`json-differ` is available from Maven Central.

### Maven

``` xml
<dependency>
    <groupId>io.github.nigalranieri</groupId>
    <artifactId>json-differ</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

**Groovy DSL**

``` groovy
dependencies {
    implementation 'io.github.nigalranieri:json-differ:1.0.0'
}
```

**Kotlin DSL**

``` kotlin
dependencies {
    implementation("io.github.nigalranieri:json-differ:1.0.0")
}
```

## Quick Start

Compare two JSON documents:

``` java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

String expected = "{\"name\":\"Alice\",\"age\":30}";
String actual = "{\"name\":\"Bob\",\"age\":30}";

ComparisonResult result = JsonCompare.compare(expected, actual);

if (!result.isEqual()) {
  System.out.println(result);
}
```

For a simple equality check:

``` java
boolean equal = JsonCompare.areEqual(expected, actual);
```

## Configuration

Use `JsonCompare.builder()` to customize how differences are detected.

### Ignore paths

Ignore a value or complete subtree:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.metadata.timestamp")
        .compare(expected, actual);
```

Wildcards and recursive wildcards are supported:

``` java
.ignorePath("$.users[*].timestamp")
.ignorePath("$.**.requestId")
```

### Include paths

Restrict comparison to a path and its descendants:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .includePath("$.user")
        .compare(expected, actual);
```

Multiple included paths may be configured:

``` java
JsonCompare.builder()
    .includePath("$.user.profile")
    .includePath("$.settings")
    .compare(expected, actual);
```

Wildcards use the same syntax as other path-aware options:

``` java
.includePath("$.users[*].profile")
```

When include paths are configured, unrelated parts of the document are
outside the comparison scope. Ancestors required to reach an included
path may be traversed without making their unrelated contents part of
the comparison.

Ignored paths take precedence over included paths.

### Ignore array order

Arrays are order-sensitive by default.

Ignore order for all arrays:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreArrayOrder()
        .compare(expected, actual);
```

Or only at selected paths:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreArrayOrder("$.users")
        .compare(expected, actual);
```

Wildcards are supported:

``` java
.ignoreArrayOrder("$.groups[*].users")
```

Duplicate elements remain significant.

Unordered comparison of large arrays containing complex objects can be
significantly more expensive than ordered comparison because elements
must be matched across the two arrays.

### Treat `null` and missing fields as equal

By default, an explicit JSON `null` and a missing object field are
different.

Enable equivalence globally:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .treatNullAndMissingAsEqual()
        .compare(expected, actual);
```

Or at selected paths:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .treatNullAndMissingAsEqual("$.users[*].nickname")
        .compare(expected, actual);
```

This option applies to object fields and does not change array
semantics.

### Numeric tolerance

Numbers are compared exactly by default.

Configure a global absolute tolerance:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .numericTolerance(0.01)
        .compare(expected, actual);
```

Configure path-specific tolerances:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .numericTolerance(0.01)
        .numericTolerance("$.measurements[*].value", 0.1)
        .numericTolerance("$.price", 0.001)
        .compare(expected, actual);
```

A matching path-specific tolerance takes precedence over the global
tolerance. If multiple path-specific rules match the same path, the last
configured matching tolerance is used.

Tolerances must be non-negative and finite.

### Ignore string case

Strings are case-sensitive by default.

Ignore case globally:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreCase()
        .compare(expected, actual);
```

Or only at selected paths:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignoreCase("$.users[*].email")
        .compare(expected, actual);
```

This applies only to string values. Object field names remain
case-sensitive.

When case-sensitive comparison is active, two strings that differ only
by letter case are reported as `CASE_MISMATCH`. If ignore-case applies
at that path, they are considered equal.

### Combine options

Comparison rules can be combined:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .includePath("$.users")
        .ignorePath("$.users[*].metadata")
        .ignoreArrayOrder("$.users")
        .treatNullAndMissingAsEqual("$.users[*].nickname")
        .numericTolerance("$.users[*].score", 0.01)
        .ignoreCase("$.users[*].email")
        .compare(expected, actual);
```

Ignored paths take precedence over other comparison rules at the same
path.

Global boolean rules remain global. A path-specific list for the same
option does not narrow or disable a rule that has been enabled globally.

### Reusable comparators

Build a reusable comparator when many comparisons use the same
comparison rules:

``` java
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

A built comparator keeps the configuration it was created with and is
unaffected by subsequent changes to the builder.

## JSON and YAML Configuration

The same behavior can be configured declaratively. The configuration
model has three top-level stages.

### Configuration lifecycle

``` text
comparison -> detects differences
result     -> retains selected differences
output     -> renders the retained result
```

`comparison` changes the comparison itself.

`result` is applied after comparison and filters the resulting
`ComparisonResult`.

`output` affects presentation only and does not change the structured
differences.

All sections and settings are optional.

### Loading configuration

Load a JSON or YAML configuration file:

``` java
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfigLoader;

JsonDifferConfig config =
    JsonDifferConfigLoader.load(Paths.get("json-differ.json"));
```

A `.json` file is parsed as JSON. Other supported configuration files
use YAML parsing.

Configuration can also be loaded directly from text:

``` java
JsonDifferConfig jsonConfig =
    JsonDifferConfigLoader.loadJson(json);

JsonDifferConfig yamlConfig =
    JsonDifferConfigLoader.loadYaml(yaml);
```

To perform a complete configured comparison, including configured result
filtering:

``` java
ComparisonResult result =
    JsonCompare.compare(expected, actual, config);
```

File inputs are supported as well:

``` java
ComparisonResult result =
    JsonCompare.compare(expectedPath, actualPath, config);
```

Output remains a separate step:

``` java
String formatted =
    config.getOutput().format(result);
```

If only reusable comparison behavior is needed:

``` java
JsonComparator comparator =
    JsonCompare.comparatorFromConfig(config);
```

`comparatorFromConfig(...)` creates a comparator from the `comparison`
section. It does not apply the `result` or `output` sections. Use
`JsonCompare.compare(..., config)` when configured result filtering
should also be applied.

### JSON example

``` json
{
  "comparison": {
    "ignorePaths": [
      "$.metadata.requestId",
      "$.metadata.timestamp"
    ],
    "includePaths": [],
    "arrayOrder": {
      "ignoreGlobally": false,
      "ignoreAt": [
        "$.roles",
        "$.groups[*].members"
      ]
    },
    "nullAndMissing": {
      "equalGlobally": false,
      "equalAt": [
        "$.user.nickname"
      ]
    },
    "numericTolerance": {
      "global": 0.01,
      "paths": {
        "$.user.score": 0.5,
        "$.measurements[*].value": 0.1
      }
    },
    "ignoreCase": {
      "globally": false,
      "paths": [
        "$.user.email",
        "$.users[*].username"
      ]
    }
  },
  "result": {
    "types": [
      "VALUE_MISMATCH",
      "CASE_MISMATCH",
      "MISSING_FIELD"
    ],
    "valueMismatchPattern": "^[^@]+@[^@]+$"
  },
  "output": {
    "format": "GROUPED",
    "columns": {
      "maxCellWidth": 40,
      "expectedLabel": "Expected",
      "actualLabel": "Actual"
    }
  }
}
```

### YAML example

``` yaml
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

result:
  types:
    - VALUE_MISMATCH
    - CASE_MISMATCH
    - MISSING_FIELD
  valueMismatchPattern: "^[^@]+@[^@]+$"

output:
  format: grouped
  columns:
    maxCellWidth: 40
    expectedLabel: Expected
    actualLabel: Actual
```

### Comparison options

  -------------------------------------------------------------------------------------------
Option                                      Default                 Description
  ------------------------------------------- ----------------------- -----------------------
`comparison.ignorePaths`                    empty                   Paths or patterns whose
values or subtrees are
ignored.

`comparison.includePaths`                   empty                   Restricts comparison to
listed paths and their
descendants. Empty
means the whole
document is in scope.

`comparison.arrayOrder.ignoreGlobally`      `false`                 Ignores element order
for every array.

`comparison.arrayOrder.ignoreAt`            empty                   Ignores element order
for arrays matching the
listed paths.

`comparison.nullAndMissing.equalGlobally`   `false`                 Treats JSON `null` and
a missing object field
as equal globally.

`comparison.nullAndMissing.equalAt`         empty                   Enables null/missing
equivalence at matching
paths.

`comparison.numericTolerance.global`        none                    Global absolute numeric
tolerance.

`comparison.numericTolerance.paths`         empty                   Path-specific numeric
tolerances.

`comparison.ignoreCase.globally`            `false`                 Compares string values
without considering
case globally.

`comparison.ignoreCase.paths`               empty                   Enables
case-insensitive string
comparison at matching
paths.
  -------------------------------------------------------------------------------------------

Path-specific configuration uses the same syntax as the builder API.

For numeric tolerance, matching path-specific values override the global
tolerance. If multiple path-specific tolerance patterns match, the last
configured matching rule is used.

For boolean global/path rules, enabling the global option applies it
everywhere it is applicable. Paths configured for that same option are
then redundant; they do not narrow or override the global setting.

Ignored paths take precedence over included paths and other comparison
rules.

### Result options

  -------------------------------------------------------------------------------
Option                          Default                 Description
  ------------------------------- ----------------------- -----------------------
`result.types`                  empty                   Difference types
eligible to be
retained. Empty means
all types are eligible.

`result.valueMismatchPattern`   none                    Regex used to further
restrict eligible
`VALUE_MISMATCH`
differences.
  -------------------------------------------------------------------------------

Result filtering happens after differences have been detected.

When `types` is empty or absent, every difference type is eligible. When
populated, only the listed types are eligible.

`valueMismatchPattern` affects `VALUE_MISMATCH` only. A value mismatch
is retained when either the expected or actual JSON string matches the
entire regular expression. Non-string value mismatches do not match the
pattern. Other eligible difference types are unaffected.

When `types` and `valueMismatchPattern` are both configured, the type
list establishes eligibility first and the regex then further restricts
eligible `VALUE_MISMATCH` entries.

### Output options

  --------------------------------------------------------------------------------
Option                           Default                 Description
  -------------------------------- ----------------------- -----------------------
`output.format`                  `TRAVERSAL`             Presentation mode:
`TRAVERSAL` or
`GROUPED`,
case-insensitively.

`output.columns.maxCellWidth`    `40`                    Maximum formatted
table-cell width before
wrapping. Must be
greater than zero.

`output.columns.expectedLabel`   `EXPECTED`              Header used for the
expected-value column.

`output.columns.actualLabel`     `ACTUAL`                Header used for the
actual-value column.
  --------------------------------------------------------------------------------

Output configuration affects rendering only.

### Validation

Configuration is validated rather than silently ignored.

-   Unknown configuration properties are rejected.
-   Malformed JSON or YAML is rejected.
-   Paths must use valid json-differ path syntax and start with `$`.
-   Numeric tolerances must be non-negative and finite.
-   `maxCellWidth` must be greater than zero.
-   Invalid enum values are rejected.
-   Invalid regular expressions are rejected when the configured result
    filter is applied.
-   Blank JSON or YAML text is treated as an empty/default
    configuration.
-   Blank JSON or YAML files are also treated as an empty/default
    configuration.
-   Omitted or explicitly `null` top-level configuration sections fall
    back to their defaults.

## Path Syntax

All path-specific comparison rules use the same path syntax. Paths start
from `$`, the document root.

This applies to:

-   `ignorePath(...)`
-   `includePath(...)`
-   `ignoreArrayOrder(...)`
-   `treatNullAndMissingAsEqual(...)`
-   `numericTolerance(...)`
-   `ignoreCase(...)`

Syntax        Meaning                                   Example
  ------------- ----------------------------------------- ------------------
`$`           Document root                             `$`
`.property`   Object property                           `$.user.name`
`[n]`         Exact array index                         `$.users[0]`
`*`           Any property at one level                 `$.*.timestamp`
`[*]`         Any array index                           `$.users[*].id`
`**`          Recursive wildcard across nested levels   `$.**.timestamp`

Examples:

``` java
// Ignore one exact property
.ignorePath("$.metadata.timestamp")

// Compare only the user subtree
.includePath("$.user")

// Ignore the timestamp of every user
.ignorePath("$.users[*].timestamp")

// Ignore timestamp wherever it appears recursively
.ignorePath("$.**.timestamp")

// Ignore array order for users arrays inside groups
.ignoreArrayOrder("$.groups[*].users")

// Treat null and missing nicknames as equivalent
.treatNullAndMissingAsEqual("$.users[*].nickname")

// Use a different tolerance for measurement values
.numericTolerance("$.measurements[*].value", 0.1)

// Compare email values without considering case
.ignoreCase("$.users[*].email")
```

Invalid paths are rejected with `IllegalArgumentException`.

## Default Comparison Semantics

`json-differ` performs strict structural comparison by default.

Without additional configuration:

-   Object property order is ignored.
-   Object fields must exist on both sides.
-   Array order is significant.
-   Array length and duplicate elements are significant.
-   JSON `null` and a missing object field are different.
-   Numbers are compared exactly.
-   Strings are case-sensitive.
-   Strings differing only by case are reported as `CASE_MISMATCH`.
-   Values of different JSON types are not considered equal.
-   No paths are ignored.
-   The whole document is in comparison scope.

For example, object property order does not affect equality:

``` json
{
  "name": "Alice",
  "age": 30
}
```

and:

``` json
{
  "age": 30,
  "name": "Alice"
}
```

are equal.

Array order does affect equality:

``` json
[1, 2, 3]
```

and:

``` json
[3, 2, 1]
```

are different unless unordered array comparison is enabled.

## Working with Differences

`JsonCompare.compare(...)` returns a `ComparisonResult`.

``` java
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

-   the JSON path where the difference was detected
-   the `DifferenceType`
-   the expected `DifferenceValue`
-   the actual `DifferenceValue`

### Difference types

Possible difference types are:

-   `VALUE_MISMATCH`
-   `CASE_MISMATCH`
-   `MISSING_FIELD`
-   `UNEXPECTED_FIELD`
-   `MISSING_ELEMENT`
-   `UNEXPECTED_ELEMENT`

`CASE_MISMATCH` is used when two string values are equal ignoring case
but differ in letter case:

``` text
"ACTIVE" vs "active" -> CASE_MISMATCH
"ACTIVE" vs "INACTIVE" -> VALUE_MISMATCH
```

When case-insensitive comparison applies at that path, the first pair is
considered equal instead.

### Difference values

`DifferenceValue` preserves the JSON value type without exposing Jackson
types through the public result API.

Supported value types are:

-   `STRING`
-   `NUMBER`
-   `BOOLEAN`
-   `OBJECT`
-   `ARRAY`
-   `NULL`
-   `MISSING`

JSON objects are exposed as immutable `Map` values and arrays as
immutable `List` values.

Missing values and explicit JSON `null` remain distinct:

``` java
difference.getExpected().isMissing();
difference.getExpected().isNull();
```

## Filtering Results

Filtering operates on an existing immutable `ComparisonResult` and
returns a new result. The original result is not modified, and retained
differences preserve their original traversal order.

### Filter by difference type

``` java
ComparisonResult filtered =
    result.filter(
        DifferenceType.VALUE_MISMATCH,
        DifferenceType.CASE_MISMATCH);
```

Only the requested difference types are retained.

### Filter value mismatches with a regular expression

``` java
import java.util.regex.Pattern;

Pattern emailPattern =
    Pattern.compile("^[^@]+@[^@]+$");

ComparisonResult emails =
    result.filterValueMismatches(emailPattern);
```

`filterValueMismatches(...)`:

-   considers only `VALUE_MISMATCH`
-   matches only JSON string values
-   retains a mismatch when either the expected or actual string matches
-   uses whole-value `Pattern.matcher(...).matches()` semantics
-   does not include `CASE_MISMATCH`
-   returns a new `ComparisonResult`

For declarative result filtering, see the [`result` configuration
section](#result-options). Its regex setting narrows `VALUE_MISMATCH`
entries while preserving other eligible difference types.

## Result Formatting

`ComparisonResult` provides human-readable table formatting in addition
to its structured API.

### Traversal format

Traversal format is the default used by `toString()`:

``` java
System.out.println(result);
```

or explicitly:

``` java
System.out.println(
    result.format(ComparisonResultFormat.TRAVERSAL));
```

Differences remain in traversal order, with the JSON path as the first
column.

### Grouped format

Grouped format organizes the rendered result by difference type:

``` java
System.out.println(
    result.format(ComparisonResultFormat.GROUPED));
```

Formatting changes presentation only. `getDifferences()` continues to
expose differences in their original traversal order.

Long paths and values wrap across multiple table lines rather than being
truncated.

### Cell width

Supply a custom maximum cell width:

``` java
String formatted =
    result.format(
        ComparisonResultFormat.TRAVERSAL,
        60);
```

The default maximum cell width is `40`.

### Custom column labels

The expected and actual column labels can also be customized:

``` java
String formatted =
    result.format(
        ComparisonResultFormat.GROUPED,
        40,
        "Source",
        "Target");
```

The defaults are `EXPECTED` and `ACTUAL`.

Configuration can provide the same customization:

``` json
{
  "output": {
    "columns": {
      "expectedLabel": "Source",
      "actualLabel": "Target"
    }
  }
}
```

## File Comparison

JSON files can be compared directly using `Path`:

``` java
import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

import java.nio.file.Path;
import java.nio.file.Paths;

Path expected = Paths.get("expected.json");
Path actual = Paths.get("actual.json");

ComparisonResult result =
    JsonCompare.compare(expected, actual);
```

Builder options work with files as well:

``` java
ComparisonResult result =
    JsonCompare.builder()
        .ignorePath("$.timestamp")
        .ignoreArrayOrder("$.users")
        .compare(expected, actual);
```

String and `Path` inputs can also be mixed, so either side may come from
an in-memory JSON document or a file:

``` java
ComparisonResult fromStringAndFile =
    JsonCompare.compare(expectedJson, actualPath);

ComparisonResult fromFileAndString =
    JsonCompare.compare(expectedPath, actualJson);
```

The same four input combinations are available through
`JsonCompare.areEqual(...)`, `JsonCompareBuilder.compare(...)`, and
`JsonComparator.compare(...)`.

A `JsonDifferConfig` can be applied to any of the four input
combinations:

``` java
ComparisonResult result =
    JsonCompare.compare(expected, actual, config);
```

Malformed JSON is reported with `InvalidJsonException`. Failures while
reading a JSON file are reported with `JsonReadException`.

## Requirements

-   Java 8 or later

Jackson is used internally for JSON parsing and configuration loading
but is not exposed through the public comparison or result APIs.

## License

Copyright © 2026 Nigal Ranieri.

Licensed under the Apache License 2.0. See [LICENSE](LICENSE) for
details.

------------------------------------------------------------------------

> *"A difference which makes no difference is no difference at all."*\
> --- William James
