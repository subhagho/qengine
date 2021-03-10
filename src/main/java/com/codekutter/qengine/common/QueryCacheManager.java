package com.codekutter.qengine.common;

import com.codekutter.qengine.model.ClassIndex;

import java.util.HashMap;
import java.util.Map;

public class QueryCacheManager {
    private final Map<Class<?>, ClassIndex<?>> classIndex = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ClassIndex<T> getClassIndex(Class<T> type) {
        if (!classIndex.containsKey(type)) {
            ClassIndex<T> index = new ClassIndex<>(type);
            index.setup();
            classIndex.put(type, index);
        }
        return (ClassIndex<T>) classIndex.get(type);
    }

    private static final QueryCacheManager __instance = new QueryCacheManager();

    public static QueryCacheManager get() {
        return __instance;
    }
}
