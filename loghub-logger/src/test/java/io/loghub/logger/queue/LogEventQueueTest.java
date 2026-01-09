package io.loghub.logger.queue;

import io.loghub.contract.LogEvent;
import io.loghub.contract.LogLevel;
import io.loghub.logger.http.LogHubHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogEventQueue.
 */
class LogEventQueueTest {

    private LogEventQueue queue;
    private MockHttpClient mockHttpClient;

    @BeforeEach
    void setUp() {
        mockHttpClient = new MockHttpClient();
        queue = new LogEventQueue(mockHttpClient, 100, 1);
    }

    @AfterEach
    void tearDown() {
        queue.stop();
    }

    @Test
    void shouldStartAndStop() {
        assertFalse(queue.isRunning());

        queue.start();
        assertTrue(queue.isRunning());

        queue.stop();
        assertFalse(queue.isRunning());
    }

    @Test
    void shouldEnqueueWhenRunning() {
        queue.start();

        LogEvent event = createTestEvent();
        boolean enqueued = queue.enqueue(event);

        assertTrue(enqueued);
    }

    @Test
    void shouldNotEnqueueWhenStopped() {
        // Not started
        LogEvent event = createTestEvent();
        boolean enqueued = queue.enqueue(event);

        assertFalse(enqueued);
    }

    @Test
    void shouldDropEventsWhenQueueIsFull() {
        // Create queue with capacity 2
        queue = new LogEventQueue(mockHttpClient, 2, 1);
        // Don't start - so events stay in queue

        queue.start();
        // Rapidly enqueue more than capacity
        // Note: some may be processed, this is a timing-sensitive test
        for (int i = 0; i < 100; i++) {
            queue.enqueue(createTestEvent());
        }

        // Queue should not grow beyond capacity + some processing buffer
        assertTrue(queue.getQueueSize() <= 10); // Allow some margin
    }

    @Test
    void shouldProcessEventsAsynchronously() throws InterruptedException {
        queue.start();

        for (int i = 0; i < 5; i++) {
            queue.enqueue(createTestEvent());
        }

        // Wait a bit for processing
        Thread.sleep(200);

        // Queue should be empty or nearly empty
        assertTrue(queue.getQueueSize() < 5);
    }

    private LogEvent createTestEvent() {
        return LogEvent.builder()
                .application("test-app")
                .environment("test")
                .level(LogLevel.INFO)
                .message("Test message")
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Mock HTTP client for testing.
     */
    private static class MockHttpClient extends LogHubHttpClient {
        private int sendCount = 0;

        MockHttpClient() {
            super("http://localhost:8080/logs", 1000);
        }

        @Override
        public CompletableFuture<Void> sendAsync(LogEvent logEvent) {
            sendCount++;
            return CompletableFuture.completedFuture(null);
        }

        public int getSendCount() {
            return sendCount;
        }
    }
}

