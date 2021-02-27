package com.codekutter.qengine.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class ReferenceDataManager {
    private Map<String, Collection<?>> maps = new HashMap<>();

    private ReferenceDataManager() {
    }

    public ReferenceDataManager put(@NonNull String key, @NonNull Collection<?> values) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        maps.put(key, values);

        return this;
    }

    public boolean hasReferenceData(@NonNull String key) {
        return maps.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> get(@NonNull String key) {
        if (hasReferenceData(key)) {
            return (Collection<T>) maps.get(key);
        }
        return null;
    }

    public static final ReferenceDataManager __instance = new ReferenceDataManager();

    public static ReferenceDataManager get() {
        return __instance;
    }
}
