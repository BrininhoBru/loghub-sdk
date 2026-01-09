package io.loghub.logger.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.loghub.contract.LogEvent;
import io.loghub.logger.config.LogHubConfig;
import io.loghub.logger.converter.LogEventConverter;
import io.loghub.logger.http.LogHubHttpClient;
import io.loghub.logger.queue.LogEventQueue;

/**
 * Logback Appender that sends structured logs to LogHub API.
 *
 * <p>This appender:
 * <ul>
 *   <li>Captures INFO, WARN, and ERROR level logs by default</li>
 *   <li>Sends logs asynchronously to avoid blocking the application</li>
 *   <li>Never throws exceptions that could impact the application</li>
 *   <li>Enriches logs with application metadata</li>
 * </ul>
 *
 * <p>Configuration example in logback.xml:
 * <pre>{@code
 * <appender name="LOGHUB" class="io.loghub.logger.appender.HttpLogAppender">
 *     <endpoint>http://api.loghub.io/logs</endpoint>
 *     <application>my-service</application>
 *     <environment>production</environment>
 *     <timeoutMs>5000</timeoutMs>
 *     <queueCapacity>1000</queueCapacity>
 * </appender>
 * }</pre>
 */
public class HttpLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    // Configuration properties (set via logback.xml)
    private String endpoint;
    private String application = "unknown";
    private String environment = "unknown";
    private int timeoutMs = 5000;
    private int queueCapacity = 1000;
    private int workerThreads = 1;
    private boolean enabled = true;
    private Level minimumLevel = Level.INFO;

    // Internal components (initialized on start)
    private LogHubConfig config;
    private LogHubHttpClient httpClient;
    private LogEventQueue eventQueue;
    private LogEventConverter converter;

    @Override
    public void start() {
        if (!enabled) {
            addInfo("LogHub appender is disabled");
            return;
        }

        if (endpoint == null || endpoint.isBlank()) {
            addError("LogHub endpoint is required but not configured");
            return;
        }

        try {
            // Initialize configuration
            config = LogHubConfig.getInstance();
            config.setApplication(application);
            config.setEnvironment(environment);
            config.setEndpoint(endpoint);
            config.setTimeoutMs(timeoutMs);
            config.setQueueCapacity(queueCapacity);
            config.setWorkerThreads(workerThreads);
            config.setEnabled(enabled);

            // Initialize components
            httpClient = new LogHubHttpClient(endpoint, timeoutMs);
            eventQueue = new LogEventQueue(httpClient, queueCapacity, workerThreads);
            converter = new LogEventConverter(config);

            // Start the async queue
            eventQueue.start();

            super.start();
            addInfo("LogHub appender started - endpoint: " + endpoint);

        } catch (Exception e) {
            addError("Failed to start LogHub appender", e);
            // Don't throw - appender should fail silently
        }
    }

    @Override
    public void stop() {
        try {
            if (eventQueue != null) {
                eventQueue.stop();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e) {
            // Silently ignore shutdown errors
        }
        super.stop();
        addInfo("LogHub appender stopped");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted() || !enabled) {
            return;
        }

        try {
            // Filter by minimum level
            if (!isLevelEnabled(eventObject.getLevel())) {
                return;
            }

            // Convert and enqueue the event
            LogEvent logEvent = converter.convert(eventObject);
            eventQueue.enqueue(logEvent);

        } catch (Exception e) {
            // Never throw - silently ignore errors
            // Avoid recursive logging by not using addError here
        }
    }

    /**
     * Checks if the given level meets the minimum level threshold.
     *
     * @param level the log level to check
     * @return true if the level should be logged
     */
    private boolean isLevelEnabled(Level level) {
        if (level == null) {
            return false;
        }
        return level.isGreaterOrEqual(minimumLevel);
    }

    // ========== Configuration Setters (called by Logback) ==========

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMinimumLevel(String level) {
        this.minimumLevel = Level.toLevel(level, Level.INFO);
    }

    // ========== Configuration Getters ==========

    public String getEndpoint() {
        return endpoint;
    }

    public String getApplication() {
        return application;
    }

    public String getEnvironment() {
        return environment;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMinimumLevel() {
        return minimumLevel.toString();
    }
}

