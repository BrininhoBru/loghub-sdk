package io.loghub.logger.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe context for storing extra data to be included in log metadata.
 *
 * <p>This class allows you to attach additional context data as simple key-value pairs
 * that will be automatically included in the metadata of log events sent to LogHub.
 *
 * <p>Usage example:
 * <pre>{@code
 * // Add values
 * LogContext.put("userId", "12345");
 * LogContext.put("orderId", "ORD-001");
 * LogContext.put("amount", "150.99");
 *
 * try {
 *     logger.info("Processing order");
 *     // The log will include all context data in metadata
 * } finally {
 *     LogContext.clear(); // Always clear in finally block
 * }
 * }</pre>
 *
 * <p>For request-scoped context (like in web applications), use with try-finally:
 * <pre>{@code
 * @Override
 * public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
 *     try {
 *         LogContext.put("requestId", generateRequestId());
 *         LogContext.put("clientIp", request.getRemoteAddr());
 *         chain.doFilter(request, response);
 *     } finally {
 *         LogContext.clear();
 *     }
 * }
 * }</pre>
 */
public final class LogContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    private LogContext() {
        // Utility class
    }

    /**
     * Puts a string value in the context.
     *
     * @param key   the key
     * @param value the string value
     */
    public static void put(String key, String value) {
        if (key != null && value != null) {
            CONTEXT.get().put(key, value);
        }
    }

    /**
     * Puts a numeric value in the context (will be converted to string).
     *
     * @param key   the key
     * @param value the numeric value
     */
    public static void put(String key, Number value) {
        if (key != null && value != null) {
            CONTEXT.get().put(key, value.toString());
        }
    }

    /**
     * Puts a boolean value in the context (will be converted to string).
     *
     * @param key   the key
     * @param value the boolean value
     */
    public static void put(String key, Boolean value) {
        if (key != null && value != null) {
            CONTEXT.get().put(key, value.toString());
        }
    }

    /**
     * Puts all entries from the given map into the context.
     *
     * @param values the map of values to add
     */
    public static void putAll(Map<String, String> values) {
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    CONTEXT.get().put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * Gets a value from the context.
     *
     * @param key the key
     * @return the value, or null if not present
     */
    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    /**
     * Removes a value from the context.
     *
     * @param key the key to remove
     */
    public static void remove(String key) {
        if (key != null) {
            CONTEXT.get().remove(key);
        }
    }

    /**
     * Gets all context entries as an unmodifiable map.
     *
     * @return unmodifiable copy of all context entries
     */
    public static Map<String, Object> getAll() {
        Map<String, Object> contextMap = CONTEXT.get();
        if (contextMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<>(contextMap));
    }

    /**
     * Checks if the context is empty.
     *
     * @return true if no entries in context
     */
    public static boolean isEmpty() {
        return CONTEXT.get().isEmpty();
    }

    /**
     * Clears all entries from the context.
     * Always call this in a finally block to prevent memory leaks.
     */
    public static void clear() {
        CONTEXT.get().clear();
    }

    /**
     * Removes the context entirely from the current thread.
     * Use this instead of clear() when the thread will be reused (e.g., thread pools).
     */
    public static void removeContext() {
        CONTEXT.remove();
    }
}

