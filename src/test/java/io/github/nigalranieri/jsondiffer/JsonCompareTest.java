package io.github.nigalranieri.jsondiffer;

import io.github.nigalranieri.jsondiffer.exception.InvalidJsonException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonCompareTest {

    @Test
    void shouldConsiderIdenticalJsonEqual() {
        String first = "{\"name\":\"Alice\",\"age\":30}";
        String second = "{\"name\":\"Alice\",\"age\":30}";

        assertTrue(JsonCompare.equals(first, second));
    }

    @Test
    void shouldIgnoreObjectPropertyOrder() {
        String first = "{\"name\":\"Alice\",\"age\":30}";
        String second = "{\"age\":30,\"name\":\"Alice\"}";

        assertTrue(JsonCompare.equals(first, second));
    }

    @Test
    void shouldDetectDifferentValues() {
        String first = "{\"name\":\"Alice\",\"age\":30}";
        String second = "{\"name\":\"Alice\",\"age\":31}";

        assertFalse(JsonCompare.equals(first, second));
    }

    @Test
    void shouldConsiderArrayOrderSignificant() {
        String first = "{\"values\":[1,2,3]}";
        String second = "{\"values\":[3,2,1]}";

        assertFalse(JsonCompare.equals(first, second));
    }

    @Test
    void shouldRejectInvalidJson() {
        String invalid = "{\"name\":}";
        String valid = "{\"name\":\"Alice\"}";

        assertThrows(
                InvalidJsonException.class,
                () -> JsonCompare.equals(invalid, valid)
        );
    }
}