package io.loghub.logger.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogContext.
 */
class LogContextTest {

    @BeforeEach
    void setUp() {
        LogContext.clear();
    }

    @AfterEach
    void tearDown() {
        LogContext.removeContext();
    }

    @Test
    void shouldPutAndGetStringValue() {
        LogContext.put("key", "value");

        assertEquals("value", LogContext.get("key"));
    }

    @Test
    void shouldPutAndGetNumericValueAsString() {
        LogContext.put("count", 42);
        LogContext.put("price", 19.99);

        assertEquals("42", LogContext.get("count"));
        assertEquals("19.99", LogContext.get("price"));
    }

    @Test
    void shouldPutAndGetBooleanValueAsString() {
        LogContext.put("active", true);
        LogContext.put("deleted", false);

        assertEquals("true", LogContext.get("active"));
        assertEquals("false", LogContext.get("deleted"));
    }

    @Test
    void shouldPutAllFromMap() {
        Map<String, String> values = Map.of(
                "key1", "value1",
                "key2", "value2",
                "key3", "value3"
        );

        LogContext.putAll(values);

        assertEquals("value1", LogContext.get("key1"));
        assertEquals("value2", LogContext.get("key2"));
        assertEquals("value3", LogContext.get("key3"));
    }

    @Test
    void shouldRemoveValue() {
        LogContext.put("key", "value");
        assertNotNull(LogContext.get("key"));

        LogContext.remove("key");

        assertNull(LogContext.get("key"));
    }

    @Test
    void shouldClearAllValues() {
        LogContext.put("key1", "value1");
        LogContext.put("key2", "value2");
        assertFalse(LogContext.isEmpty());

        LogContext.clear();

        assertTrue(LogContext.isEmpty());
        assertNull(LogContext.get("key1"));
        assertNull(LogContext.get("key2"));
    }

    @Test
    void shouldGetAllAsUnmodifiableMap() {
        LogContext.put("key1", "value1");
        LogContext.put("key2", "value2");

        Map<String, Object> all = LogContext.getAll();

        assertEquals(2, all.size());
        assertEquals("value1", all.get("key1"));
        assertEquals("value2", all.get("key2"));

        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> all.put("key3", "value3"));
    }

    @Test
    void shouldReturnEmptyMapWhenEmpty() {
        Map<String, Object> all = LogContext.getAll();

        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void shouldIgnoreNullKey() {
        LogContext.put(null, "value");

        assertTrue(LogContext.isEmpty());
    }

    @Test
    void shouldIgnoreNullValue() {
        LogContext.put("key", (String) null);

        assertTrue(LogContext.isEmpty());
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Main thread sets a value
        LogContext.put("mainThread", "mainValue");

        // Another thread should have its own context
        Thread otherThread = new Thread(() -> {
            LogContext.put("otherThread", "otherValue");
            assertEquals("otherValue", LogContext.get("otherThread"));
            assertNull(LogContext.get("mainThread")); // Should not see main thread's value
            LogContext.clear();
        });

        otherThread.start();
        otherThread.join();

        // Main thread should still have its value
        assertEquals("mainValue", LogContext.get("mainThread"));
        assertNull(LogContext.get("otherThread")); // Should not see other thread's value
    }
}

