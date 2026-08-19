package io.github.nigalranieri.jsondiffer.internal.path;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import io.github.nigalranieri.jsondiffer.result.DifferenceValueType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DifferenceValueTest {

  @Test
  void shouldDistinguishNullFromMissing() {
    DifferenceValue missing = DifferenceValue.missing();
    DifferenceValue nullValue = DifferenceValue.ofNull();

    assertEquals(DifferenceValueType.MISSING, missing.getType());
    assertEquals(DifferenceValueType.NULL, nullValue.getType());

    assertTrue(missing.isMissing());
    assertFalse(missing.isNull());

    assertFalse(nullValue.isMissing());
    assertTrue(nullValue.isNull());
  }

  @Test
  void shouldBeEqualWhenTypeAndValueAreEqual() {
    DifferenceValue first = DifferenceValue.of(DifferenceValueType.STRING, "Alice");
    DifferenceValue second = DifferenceValue.of(DifferenceValueType.STRING, "Alice");

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void shouldNotConsiderNullAndMissingEqual() {
    assertNotEquals(DifferenceValue.ofNull(), DifferenceValue.missing());
  }

  @Test
  void shouldRejectMissingTypeThroughGenericFactory() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DifferenceValue.of(DifferenceValueType.MISSING, "value"));
  }

  @Test
  void shouldRejectNullTypeThroughGenericFactory() {
    assertThrows(
        IllegalArgumentException.class, () -> DifferenceValue.of(DifferenceValueType.NULL, null));
  }

  @Test
  void shouldMakeListValueImmutable() {
    List<Object> values = new ArrayList<>();
    values.add("Alice");

    DifferenceValue differenceValue = DifferenceValue.of(DifferenceValueType.ARRAY, values);

    List<?> result = (List<?>) differenceValue.getValue();

    assertThrows(UnsupportedOperationException.class, () -> ((List<Object>) result).add("Bob"));
  }

  @Test
  void shouldNotChangeWhenOriginalListIsModified() {
    List<Object> values = new ArrayList<>();
    values.add("Alice");

    DifferenceValue differenceValue = DifferenceValue.of(DifferenceValueType.ARRAY, values);

    values.add("Bob");

    List<?> result = (List<?>) differenceValue.getValue();

    assertEquals(1, result.size());
    assertEquals("Alice", result.get(0));
  }

  @Test
  void shouldMakeNestedCollectionsImmutable() {
    Map<String, Object> user = new LinkedHashMap<>();
    user.put("name", "Alice");

    List<Object> users = new ArrayList<>();
    users.add(user);

    DifferenceValue differenceValue = DifferenceValue.of(DifferenceValueType.ARRAY, users);

    List<?> result = (List<?>) differenceValue.getValue();
    Map<?, ?> nestedUser = (Map<?, ?>) result.get(0);

    assertThrows(
        UnsupportedOperationException.class,
        () -> ((Map<Object, Object>) nestedUser).put("age", 30));
  }
}
