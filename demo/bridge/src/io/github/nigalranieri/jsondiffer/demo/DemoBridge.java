package io.github.nigalranieri.jsondiffer.demo;

import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfig;
import io.github.nigalranieri.jsondiffer.config.JsonDifferConfigLoader;
import io.github.nigalranieri.jsondiffer.result.ComparisonResult;

public final class DemoBridge {

    private DemoBridge() {}

    public static String compare(
            String expected,
            String actual,
            String jsonConfig) {

        try {
            JsonDifferConfig config =
                    JsonDifferConfigLoader.loadJson(jsonConfig);

            ComparisonResult result =
                    JsonCompare.compare(expected, actual, config);

            return config.getOutput().format(result);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}