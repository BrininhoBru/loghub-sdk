package io.loghub.logger.converter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.loghub.contract.LogEvent;
import io.loghub.contract.LogLevel;
import io.loghub.contract.SdkInfo;
import io.loghub.logger.config.LogHubConfig;
import io.loghub.logger.util.SdkVersion;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Converter that transforms Logback ILoggingEvent to LogHub LogEvent.
 * Handles log enrichment with application context, environment, and SDK info.
 */
public final class LogEventConverter {

    private static final String TRACE_ID_KEY = "traceId";

    private final LogHubConfig config;
    private final SdkInfo sdkInfo;

    /**
     * Creates a new converter with the given configuration.
     *
     * @param config the LogHub configuration
     */
    public LogEventConverter(LogHubConfig config) {
        this.config = config;
        this.sdkInfo = SdkInfo.builder()
                .language(SdkVersion.getLanguage())
                .version(SdkVersion.getVersion())
                .build();
    }

    /**
     * Converts a Logback event to a LogHub event.
     *
     * @param loggingEvent the Logback logging event
     * @return the converted LogEvent
     */
    public LogEvent convert(ILoggingEvent loggingEvent) {
        return LogEvent.builder()
                .application(config.getApplication())
                .environment(config.getEnvironment())
                .level(convertLevel(loggingEvent.getLevel()))
                .message(loggingEvent.getFormattedMessage())
                .timestamp(Instant.ofEpochMilli(loggingEvent.getTimeStamp()))
                .traceId(extractTraceId(loggingEvent))
                .metadata(extractMetadata(loggingEvent))
                .sdk(sdkInfo)
                .build();
    }

    /**
     * Converts Logback Level to LogHub LogLevel.
     *
     * @param level the Logback level
     * @return the corresponding LogLevel
     */
    private LogLevel convertLevel(Level level) {
        if (level == null) {
            return LogLevel.INFO;
        }
        return switch (level.toInt()) {
            case Level.TRACE_INT -> LogLevel.TRACE;
            case Level.DEBUG_INT -> LogLevel.DEBUG;
            case Level.INFO_INT -> LogLevel.INFO;
            case Level.WARN_INT -> LogLevel.WARN;
            case Level.ERROR_INT -> LogLevel.ERROR;
            default -> LogLevel.INFO;
        };
    }

    /**
     * Extracts trace ID from MDC if present.
     *
     * @param loggingEvent the logging event
     * @return the trace ID or null
     */
    private String extractTraceId(ILoggingEvent loggingEvent) {
        Map<String, String> mdcMap = loggingEvent.getMDCPropertyMap();
        if (mdcMap != null && mdcMap.containsKey(TRACE_ID_KEY)) {
            return mdcMap.get(TRACE_ID_KEY);
        }
        // Also check current MDC context
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * Extracts additional metadata from MDC (excluding traceId).
     *
     * @param loggingEvent the logging event
     * @return metadata map or null if empty
     */
    private Map<String, Object> extractMetadata(ILoggingEvent loggingEvent) {
        Map<String, String> mdcMap = loggingEvent.getMDCPropertyMap();
        if (mdcMap == null || mdcMap.isEmpty()) {
            return null;
        }

        Map<String, Object> metadata = new HashMap<>();
        for (Map.Entry<String, String> entry : mdcMap.entrySet()) {
            // Skip traceId as it has its own field
            if (!TRACE_ID_KEY.equals(entry.getKey())) {
                metadata.put(entry.getKey(), entry.getValue());
            }
        }

        // Add exception info if present
        if (loggingEvent.getThrowableProxy() != null) {
            metadata.put("exception.class", loggingEvent.getThrowableProxy().getClassName());
            metadata.put("exception.message", loggingEvent.getThrowableProxy().getMessage());
        }

        // Add logger name
        metadata.put("logger", loggingEvent.getLoggerName());

        // Add thread name
        metadata.put("thread", loggingEvent.getThreadName());

        return metadata.isEmpty() ? null : metadata;
    }
}

