package io.loghub.logger.converter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import io.loghub.contract.LogEvent;
import io.loghub.contract.LogLevel;
import io.loghub.contract.SdkInfo;
import io.loghub.logger.config.LogHubConfig;
import io.loghub.logger.context.LogContext;
import io.loghub.logger.util.SdkVersion;
import io.loghub.logger.util.SensitiveDataMasker;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Converter that transforms Logback ILoggingEvent to LogHub LogEvent.
 * Handles log enrichment with application context, environment, and SDK info.
 * Automatically masks sensitive data in messages and metadata.
 */
public final class LogEventConverter {

    private static final String TRACE_ID_KEY = "traceId";

    private final LogHubConfig config;
    private final SdkInfo sdkInfo;
    private final boolean maskSensitiveData;

    /**
     * Creates a new converter with the given configuration.
     * Sensitive data masking is enabled by default.
     *
     * @param config the LogHub configuration
     */
    public LogEventConverter(LogHubConfig config) {
        this(config, true);
    }

    /**
     * Creates a new converter with the given configuration.
     *
     * @param config            the LogHub configuration
     * @param maskSensitiveData whether to mask sensitive data
     */
    public LogEventConverter(LogHubConfig config, boolean maskSensitiveData) {
        this.config = config;
        this.maskSensitiveData = maskSensitiveData;
        this.sdkInfo = SdkInfo.builder()
                .language(SdkVersion.getLanguage())
                .version(SdkVersion.getVersion())
                .build();
    }

    /**
     * Converts a Logback event to a LogHub event.
     * Sensitive data is automatically masked if enabled.
     *
     * @param loggingEvent the Logback logging event
     * @return the converted LogEvent
     */
    public LogEvent convert(ILoggingEvent loggingEvent) {
        String message = loggingEvent.getFormattedMessage();

        // Mask sensitive patterns in message if enabled
        if (maskSensitiveData) {
            message = SensitiveDataMasker.mask(message);
        }

        return LogEvent.builder()
                .application(config.getApplication())
                .environment(config.getEnvironment())
                .level(convertLevel(loggingEvent.getLevel()))
                .message(message)
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
     * Extracts additional metadata from MDC, LogContext, and exception info.
     * All values are converted to strings and sensitive fields are masked.
     *
     * @param loggingEvent the logging event
     * @return metadata map with string values
     */
    private Map<String, String> extractMetadata(ILoggingEvent loggingEvent) {
        Map<String, String> metadata = new HashMap<>();

        // 1. Add LogContext entries (converted to strings, masked if sensitive)
        Map<String, Object> contextData = LogContext.getAll();
        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());

            if (maskSensitiveData) {
                value = SensitiveDataMasker.maskIfSensitive(key, value);
            }

            metadata.put(key, value);
        }

        // 2. Add MDC entries (excluding traceId, masked if sensitive)
        Map<String, String> mdcMap = loggingEvent.getMDCPropertyMap();
        if (mdcMap != null && !mdcMap.isEmpty()) {
            for (Map.Entry<String, String> entry : mdcMap.entrySet()) {
                String key = entry.getKey();
                if (!TRACE_ID_KEY.equals(key)) {
                    String value = entry.getValue();

                    if (maskSensitiveData) {
                        value = SensitiveDataMasker.maskIfSensitive(key, value);
                    }

                    metadata.put(key, value);
                }
            }
        }

        // 3. Add exception info if present (as simple string fields)
        IThrowableProxy throwableProxy = loggingEvent.getThrowableProxy();
        if (throwableProxy != null) {
            metadata.put("exception.class", throwableProxy.getClassName());
            String exceptionMessage = throwableProxy.getMessage();

            // Mask sensitive patterns in exception message
            if (maskSensitiveData && exceptionMessage != null) {
                exceptionMessage = SensitiveDataMasker.mask(exceptionMessage);
            }

            metadata.put("exception.message", exceptionMessage);
        }

        // 4. Add logger context info
        metadata.put("logger", loggingEvent.getLoggerName());
        metadata.put("thread", loggingEvent.getThreadName());

        return metadata;
    }
}

