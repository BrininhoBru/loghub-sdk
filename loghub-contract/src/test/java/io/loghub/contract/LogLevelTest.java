package io.loghub.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogLevel enum.
 */
class LogLevelTest {

    @Test
    void shouldReturnCorrectValue() {
        assertEquals("TRACE", LogLevel.TRACE.getValue());
        assertEquals("DEBUG", LogLevel.DEBUG.getValue());
        assertEquals("INFO", LogLevel.INFO.getValue());
        assertEquals("WARN", LogLevel.WARN.getValue());
        assertEquals("ERROR", LogLevel.ERROR.getValue());
    }

    @Test
    void shouldCreateFromValueCaseInsensitive() {
        assertEquals(LogLevel.INFO, LogLevel.fromValue("INFO"));
        assertEquals(LogLevel.INFO, LogLevel.fromValue("info"));
        assertEquals(LogLevel.INFO, LogLevel.fromValue("Info"));
    }

    @Test
    void shouldReturnNullForNullValue() {
        assertNull(LogLevel.fromValue(null));
    }

    @Test
    void shouldThrowForUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> LogLevel.fromValue("UNKNOWN"));
    }

    @Test
    void shouldReturnValueAsString() {
        assertEquals("ERROR", LogLevel.ERROR.toString());
    }
}

