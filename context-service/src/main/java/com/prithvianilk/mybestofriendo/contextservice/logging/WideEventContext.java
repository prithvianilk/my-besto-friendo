package com.prithvianilk.mybestofriendo.contextservice.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class WideEventContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    private WideEventContext() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Mergeable<T>> void enrich(String key, T value) {
        Map<String, Object> context = CONTEXT.get();
        Object existing = context.get(key);

        if (existing != null && existing.getClass().isInstance(value)) {
            T merged = ((T) existing).merge(value);
            context.put(key, merged);
        } else {
            context.put(key, value);
        }
    }

    public static void put(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static Map<String, Object> getContext() {
        return Collections.unmodifiableMap(new HashMap<>(CONTEXT.get()));
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
