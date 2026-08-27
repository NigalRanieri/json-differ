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
            String yaml) {

        try {
            JsonDifferConfig config =
                    JsonDifferConfigLoader.load(yaml);

            ComparisonResult result =
                    JsonCompare.fromConfig(config)
                            .compare(expected, actual);

            return config.getOutput().format(result);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}