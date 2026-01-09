package io.loghub.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Model representing a structured log event.
 * This is the core contract for log messages in the LogHub ecosystem.
 *
 * <p>JSON contract:
 * <pre>{@code
 * {
 *   "application": "string",
 *   "environment": "string",
 *   "level": "TRACE | DEBUG | INFO | WARN | ERROR",
 *   "message": "string",
 *   "timestamp": "ISO-8601 UTC",
 *   "traceId": "string (optional)",
 *   "metadata": "object (optional)",
 *   "sdk": {
 *     "language": "string",
 *     "version": "string"
 *   }
 * }
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class LogEvent {

    @JsonProperty("application")
    private final String application;

    @JsonProperty("environment")
    private final String environment;

    @JsonProperty("level")
    private final LogLevel level;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant timestamp;

    @JsonProperty("traceId")
    private final String traceId;

    @JsonProperty("metadata")
    private final Map<String, Object> metadata;

    @JsonProperty("sdk")
    private final SdkInfo sdk;

    /**
     * Default constructor for JSON deserialization.
     */
    public LogEvent() {
        this.application = null;
        this.environment = null;
        this.level = null;
        this.message = null;
        this.timestamp = null;
        this.traceId = null;
        this.metadata = null;
        this.sdk = null;
    }

    /**
     * Creates a new LogEvent instance with all fields.
     */
    private LogEvent(String application, String environment, LogLevel level,
                     String message, Instant timestamp, String traceId,
                     Map<String, Object> metadata, SdkInfo sdk) {
        this.application = application;
        this.environment = environment;
        this.level = level;
        this.message = message;
        this.timestamp = timestamp;
        this.traceId = traceId;
        this.metadata = metadata;
        this.sdk = sdk;
    }

    /**
     * Gets the application name.
     *
     * @return the application name
     */
    public String getApplication() {
        return application;
    }

    /**
     * Gets the environment (e.g., "production", "staging", "development").
     *
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public LogLevel getLevel() {
        return level;
    }

    /**
     * Gets the log message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the timestamp when the log event was created.
     *
     * @return the timestamp in UTC
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the trace ID for distributed tracing (optional).
     *
     * @return the trace ID, or null if not present
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Gets additional metadata as key-value pairs (optional).
     *
     * @return the metadata map, or null if not present
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Gets the SDK information.
     *
     * @return the SDK info
     */
    public SdkInfo getSdk() {
        return sdk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEvent logEvent = (LogEvent) o;
        return Objects.equals(application, logEvent.application) &&
               Objects.equals(environment, logEvent.environment) &&
               level == logEvent.level &&
               Objects.equals(message, logEvent.message) &&
               Objects.equals(timestamp, logEvent.timestamp) &&
               Objects.equals(traceId, logEvent.traceId) &&
               Objects.equals(metadata, logEvent.metadata) &&
               Objects.equals(sdk, logEvent.sdk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(application, environment, level, message,
                timestamp, traceId, metadata, sdk);
    }

    @Override
    public String toString() {
        return "LogEvent{" +
               "application='" + application + '\'' +
               ", environment='" + environment + '\'' +
               ", level=" + level +
               ", message='" + message + '\'' +
               ", timestamp=" + timestamp +
               ", traceId='" + traceId + '\'' +
               ", metadata=" + metadata +
               ", sdk=" + sdk +
               '}';
    }

    /**
     * Creates a new Builder instance.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating LogEvent instances.
     */
    public static final class Builder {
        private String application;
        private String environment;
        private LogLevel level;
        private String message;
        private Instant timestamp;
        private String traceId;
        private Map<String, Object> metadata;
        private SdkInfo sdk;

        private Builder() {
        }

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public Builder environment(String environment) {
            this.environment = environment;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder sdk(SdkInfo sdk) {
            this.sdk = sdk;
            return this;
        }

        public LogEvent build() {
            return new LogEvent(application, environment, level, message,
                    timestamp, traceId, metadata, sdk);
        }
    }
}

