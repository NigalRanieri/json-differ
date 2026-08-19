package io.github.nigalranieri.jsondiffer.internal.path;

import static org.junit.jupiter.api.Assertions.*;

import io.github.nigalranieri.jsondiffer.result.DifferenceValue;
import io.github.nigalranieri.jsondiffer.result.DifferenceValueType;
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
}
