package io.github.nigalranieri.jsondiffer.demo;

import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class DemoBridge {

    private DemoBridge() {}

    public static String compare(String expected, String actual) {
        try {
            ComparisonResult result = JsonCompare.compare(expected, actual);
            return result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}