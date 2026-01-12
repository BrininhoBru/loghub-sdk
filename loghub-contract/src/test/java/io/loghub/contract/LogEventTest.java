package io.loghub.contract;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogEvent model.
 */
class LogEventTest {

    @Test
    void shouldCreateWithBuilder() {
        Instant now = Instant.now();
        SdkInfo sdk = new SdkInfo("java", "1.0.0");
        Map<String, String> metadata = Map.of("key", "value");

        LogEvent event = LogEvent.builder()
                .application("test-app")
                .environment("production")
                .level(LogLevel.INFO)
                .message("Test message")
                .timestamp(now)
                .traceId("trace-123")
                .metadata(metadata)
                .sdk(sdk)
                .build();

        assertEquals("test-app", event.getApplication());
        assertEquals("production", event.getEnvironment());
        assertEquals(LogLevel.INFO, event.getLevel());
        assertEquals("Test message", event.getMessage());
        assertEquals(now, event.getTimestamp());
        assertEquals("trace-123", event.getTraceId());
        assertEquals(metadata, event.getMetadata());
        assertEquals(sdk, event.getSdk());
    }

    @Test
    void shouldAllowOptionalFields() {
        LogEvent event = LogEvent.builder()
                .application("test-app")
                .environment("dev")
                .level(LogLevel.ERROR)
                .message("Error message")
                .timestamp(Instant.now())
                .build();

        assertNull(event.getTraceId());
        assertNull(event.getMetadata());
        assertNull(event.getSdk());
    }

    @Test
    void shouldBeEqualForSameValues() {
        Instant timestamp = Instant.parse("2024-01-15T10:30:00Z");

        LogEvent event1 = LogEvent.builder()
                .application("app")
                .environment("env")
                .level(LogLevel.INFO)
                .message("msg")
                .timestamp(timestamp)
                .build();

        LogEvent event2 = LogEvent.builder()
                .application("app")
                .environment("env")
                .level(LogLevel.INFO)
                .message("msg")
                .timestamp(timestamp)
                .build();

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        Instant timestamp = Instant.now();

        LogEvent event1 = LogEvent.builder()
                .application("app1")
                .level(LogLevel.INFO)
                .timestamp(timestamp)
                .build();

        LogEvent event2 = LogEvent.builder()
                .application("app2")
                .level(LogLevel.INFO)
                .timestamp(timestamp)
                .build();

        assertNotEquals(event1, event2);
    }

    @Test
    void shouldHaveToString() {
        LogEvent event = LogEvent.builder()
                .application("my-app")
                .environment("staging")
                .level(LogLevel.WARN)
                .message("Warning message")
                .timestamp(Instant.now())
                .build();

        String str = event.toString();
        assertTrue(str.contains("my-app"));
        assertTrue(str.contains("staging"));
        assertTrue(str.contains("WARN"));
        assertTrue(str.contains("Warning message"));
    }
}

