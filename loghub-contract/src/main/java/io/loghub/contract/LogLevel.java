package io.loghub.contract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing log severity levels.
 * Maps to standard logging framework levels.
 */
public enum LogLevel {

    TRACE("TRACE"),
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARN"),
    ERROR("ERROR");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LogLevel fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (LogLevel level : LogLevel.values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown log level: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}

