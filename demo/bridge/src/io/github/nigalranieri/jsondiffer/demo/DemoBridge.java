package io.github.nigalranieri.jsondiffer.demo;

import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.JsonCompareBuilder;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;
import io.github.nigalranieri.jsondiffer.result.ComparisonResultFormat;

public final class DemoBridge {

    private DemoBridge() {}

    public static String compare(
            String expected,
            String actual,
            String ignoredPaths,
            boolean ignoreArrayOrder,
            String unorderedArrayPaths,
            boolean treatNullAndMissingAsEqual,
            String numericTolerance,
            boolean grouped) {

        try {
            JsonCompareBuilder builder = JsonCompare.builder();

            applyIgnoredPaths(builder, ignoredPaths);

            if (ignoreArrayOrder) {
                builder.ignoreArrayOrder();
            } else {
                applyUnorderedArrayPaths(builder, unorderedArrayPaths);
            }

            if (treatNullAndMissingAsEqual) {
                builder.treatNullAndMissingAsEqual();
            }

            if (numericTolerance != null && !numericTolerance.trim().isEmpty()) {
                builder.numericTolerance(Double.parseDouble(numericTolerance.trim()));
            }

            ComparisonResult result = builder.compare(expected, actual);

            return result.format(
                    grouped ? ComparisonResultFormat.GROUPED : ComparisonResultFormat.TRAVERSAL);

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private static void applyIgnoredPaths(JsonCompareBuilder builder, String paths) {
        if (paths == null || paths.trim().isEmpty()) {
            return;
        }

        for (String path : paths.split("\\r?\\n")) {
            String trimmed = path.trim();

            if (!trimmed.isEmpty()) {
                builder.ignorePath(trimmed);
            }
        }
    }

    private static void applyUnorderedArrayPaths(JsonCompareBuilder builder, String paths) {
        if (paths == null || paths.trim().isEmpty()) {
            return;
        }

        for (String path : paths.split("\\r?\\n")) {
            String trimmed = path.trim();

            if (!trimmed.isEmpty()) {
                builder.ignoreArrayOrder(trimmed);
            }
        }
    }
}