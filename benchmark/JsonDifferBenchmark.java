import io.github.nigalranieri.jsondiffer.JsonCompare;
import io.github.nigalranieri.jsondiffer.JsonComparator;

public final class JsonDifferBenchmark {

    private static final int WARMUP_ITERATIONS = 2;
    private static final int MEASURED_ITERATIONS = 3;

    private JsonDifferBenchmark() {}

    public static void main(String[] args) {
        System.out.println("json-differ manual benchmark");
        System.out.println("============================");

        benchmarkFlatObject(1_000);
        benchmarkNestedObject(1_000);
        benchmarkOrderedArray(1_000);

        benchmarkUnorderedArray(100);
        benchmarkUnorderedArray(250);
        benchmarkUnorderedArray(500);
        benchmarkUnorderedArray(1_000);
        benchmarkUnorderedArray(2_000);
        benchmarkUnorderedArray(5_000);

        benchmarkRecursiveWildcard(1_000);
        benchmarkIncludedPath(1_000);
    }

    private static void benchmarkFlatObject(int size) {
        String expected = flatObject(size, false);
        String actual = flatObject(size, true);

        benchmark(
                "Flat object, " + size + " fields",
                () -> JsonCompare.compare(expected, actual));
    }

    private static void benchmarkNestedObject(int size) {
        String expected = nestedObject(size, false);
        String actual = nestedObject(size, true);

        benchmark(
                "Nested object, " + size + " leaves",
                () -> JsonCompare.compare(expected, actual));
    }

    private static void benchmarkOrderedArray(int size) {
        String expected = array(size, false);
        String actual = array(size, true);

        benchmark(
                "Ordered array, " + size + " elements",
                () -> JsonCompare.compare(expected, actual));
    }

    private static void benchmarkUnorderedArray(int size) {
        String expected = reversedObjectArray(size);
        String actual = objectArray(size);

        JsonComparator comparator =
                JsonCompare.builder()
                        .ignoreArrayOrder("$.items")
                        .build();

        benchmark(
                "Unordered array, " + size + " elements",
                () -> comparator.compare(expected, actual));
    }

    private static void benchmarkRecursiveWildcard(int size) {
        String expected = nestedObject(size, false);
        String actual = nestedObject(size, true);

        JsonComparator comparator =
                JsonCompare.builder()
                        .ignoreCase("$.root.**")
                        .build();

        benchmark(
                "Recursive wildcard, " + size + " leaves",
                () -> comparator.compare(expected, actual));
    }

    private static void benchmarkIncludedPath(int size) {
        String expected = largeDocument(size, "Alice");
        String actual = largeDocument(size, "Bob");

        JsonComparator comparator =
                JsonCompare.builder()
                        .includePath("$.selected")
                        .build();

        benchmark(
                "Included subtree, " + size + " unrelated fields",
                () -> comparator.compare(expected, actual));
    }

    private static void benchmark(String name, Runnable operation) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            operation.run();
        }

        long totalNanos = 0L;
        long minNanos = Long.MAX_VALUE;
        long maxNanos = Long.MIN_VALUE;

        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
            long start = System.nanoTime();

            operation.run();

            long elapsed = System.nanoTime() - start;

            totalNanos += elapsed;
            minNanos = Math.min(minNanos, elapsed);
            maxNanos = Math.max(maxNanos, elapsed);
        }

        double averageMillis =
                totalNanos / (double) MEASURED_ITERATIONS / 1_000_000.0;

        double minMillis = minNanos / 1_000_000.0;
        double maxMillis = maxNanos / 1_000_000.0;

        System.out.printf(
                "%-45s avg %8.3f ms | min %8.3f | max %8.3f%n",
                name,
                averageMillis,
                minMillis,
                maxMillis);
    }

    private static String flatObject(int size, boolean changeLast) {
        StringBuilder json = new StringBuilder("{");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                json.append(',');
            }

            json.append("\"field")
                    .append(i)
                    .append("\":")
                    .append(changeLast && i == size - 1 ? -1 : i);
        }

        return json.append('}').toString();
    }

    private static String nestedObject(int size, boolean changeLast) {
        StringBuilder json = new StringBuilder("{\"root\":{");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                json.append(',');
            }

            json.append("\"item")
                    .append(i)
                    .append("\":{\"value\":\"")
                    .append(changeLast && i == size - 1 ? "CHANGED" : "value" + i)
                    .append("\"}");
        }

        return json.append("}}").toString();
    }

    private static String array(int size, boolean changeLast) {
        StringBuilder json = new StringBuilder("{\"items\":[");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                json.append(',');
            }

            json.append(changeLast && i == size - 1 ? -1 : i);
        }

        return json.append("]}").toString();
    }

    private static String objectArray(int size) {
        StringBuilder json = new StringBuilder("{\"items\":[");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                json.append(',');
            }

            json.append("{\"id\":")
                    .append(i)
                    .append(",\"name\":\"item")
                    .append(i)
                    .append("\"}");
        }

        return json.append("]}").toString();
    }

    private static String reversedObjectArray(int size) {
        StringBuilder json = new StringBuilder("{\"items\":[");

        for (int i = size - 1; i >= 0; i--) {
            if (i < size - 1) {
                json.append(',');
            }

            json.append("{\"id\":")
                    .append(i)
                    .append(",\"name\":\"item")
                    .append(i)
                    .append("\"}");
        }

        return json.append("]}").toString();
    }

    private static String largeDocument(int size, String selectedName) {
        StringBuilder json =
                new StringBuilder("{\"selected\":{\"name\":\"")
                        .append(selectedName)
                        .append("\"},\"unrelated\":{");

        for (int i = 0; i < size; i++) {
            if (i > 0) {
                json.append(',');
            }

            json.append("\"field")
                    .append(i)
                    .append("\":")
                    .append(i);
        }

        return json.append("}}").toString();
    }
}