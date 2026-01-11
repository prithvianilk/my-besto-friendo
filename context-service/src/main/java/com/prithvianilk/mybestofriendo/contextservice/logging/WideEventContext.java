package com.prithvianilk.mybestofriendo.contextservice.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local context holder for wide event logging.
 * Stores key-value pairs that can be enriched throughout a request lifecycle
 * and logged at the end of processing.
 */
public final class WideEventContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private WideEventContext() {
        // Utility class, prevent instantiation
    }

    /**
     * Enriches the wide event context with a key-value pair.
     * If the key already exists and both values are Mergeable, they will be merged.
     * Otherwise, the new value replaces the existing one.
     *
     * @param key   The key to store the value under
     * @param value The Mergeable value to store or merge
     * @param <T>   The type of the Mergeable value
     */
    @SuppressWarnings("unchecked")
    public static <T extends Mergeable<T>> void enrich(String key, T value) {
        Map<String, Object> context = CONTEXT.get();
        Object existing = context.get(key);

        if (existing != null && existing.getClass().isInstance(value)) {
            // Merge with existing value
            T merged = ((T) existing).merge(value);
            context.put(key, merged);
        } else {
            // Add new value or replace incompatible type
            context.put(key, value);
        }
    }

    /**
     * Adds a simple key-value pair to the context without merging.
     *
     * @param key   The key to store the value under
     * @param value The value to store
     */
    public static void put(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    /**
     * Gets a value from the context.
     *
     * @param key The key to look up
     * @return The value, or null if not present
     */
    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    /**
     * Returns an unmodifiable view of the current context.
     *
     * @return Unmodifiable map of the current context
     */
    public static Map<String, Object> getContext() {
        return Collections.unmodifiableMap(new HashMap<>(CONTEXT.get()));
    }

    /**
     * Clears all data from the current thread's context.
     * This should be called at the end of request processing to prevent memory
     * leaks.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
