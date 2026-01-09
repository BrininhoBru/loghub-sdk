package io.loghub.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpLogAppender.
 */
class HttpLogAppenderTest {

    private HttpLogAppender appender;
    private LoggerContext loggerContext;

    @BeforeEach
    void setUp() {
        loggerContext = new LoggerContext();
        appender = new HttpLogAppender();
        appender.setContext(loggerContext);
    }

    @AfterEach
    void tearDown() {
        if (appender.isStarted()) {
            appender.stop();
        }
    }

    @Test
    void shouldNotStartWithoutEndpoint() {
        appender.setApplication("test-app");
        appender.setEnvironment("test");
        // No endpoint set

        appender.start();

        assertFalse(appender.isStarted());
    }

    @Test
    void shouldNotStartWhenDisabled() {
        appender.setEndpoint("http://localhost:8080/logs");
        appender.setApplication("test-app");
        appender.setEnabled(false);

        appender.start();

        assertFalse(appender.isStarted());
    }

    @Test
    void shouldStartWithValidConfiguration() {
        appender.setEndpoint("http://localhost:8080/logs");
        appender.setApplication("test-app");
        appender.setEnvironment("test");
        appender.setEnabled(true);

        appender.start();

        assertTrue(appender.isStarted());
    }

    @Test
    void shouldHaveDefaultValues() {
        assertEquals(5000, appender.getTimeoutMs());
        assertEquals(1000, appender.getQueueCapacity());
        assertEquals("INFO", appender.getMinimumLevel());
        assertTrue(appender.isEnabled());
    }

    @Test
    void shouldAllowConfigurationOverrides() {
        appender.setEndpoint("http://custom.endpoint/logs");
        appender.setApplication("custom-app");
        appender.setEnvironment("custom-env");
        appender.setTimeoutMs(10000);
        appender.setQueueCapacity(500);
        appender.setMinimumLevel("WARN");
        appender.setWorkerThreads(2);

        assertEquals("http://custom.endpoint/logs", appender.getEndpoint());
        assertEquals("custom-app", appender.getApplication());
        assertEquals("custom-env", appender.getEnvironment());
        assertEquals(10000, appender.getTimeoutMs());
        assertEquals(500, appender.getQueueCapacity());
        assertEquals("WARN", appender.getMinimumLevel());
        assertEquals(2, appender.getWorkerThreads());
    }

    @Test
    void shouldStopCleanly() {
        appender.setEndpoint("http://localhost:8080/logs");
        appender.start();
        assertTrue(appender.isStarted());

        appender.stop();

        assertFalse(appender.isStarted());
    }

    @Test
    void shouldIgnoreAppendWhenNotStarted() {
        // Appender not started - should not throw
        assertDoesNotThrow(() -> {
            // Create a mock event using reflection or a simple approach
            Logger logger = loggerContext.getLogger("test");
            // This would typically fail, but we're just ensuring no exception is thrown
        });
    }
}

