package com.codekutter.qengine.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;
import org.ehcache.CacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReferenceDataManager {
    private ObjectState state = new ObjectState();
    private Map<String, Collection<?>> maps = new HashMap<>();
    private Map<String, ExternalDataList> external = new HashMap<>();
    private CacheManager cacheManager;

    private ReferenceDataManager() {
    }

    private void init() throws ConfigurationException {

    }

    public ReferenceDataManager put(@NonNull String key, @NonNull Collection<?> values) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        maps.put(key, values);

        return this;
    }

    public boolean hasReferenceData(@NonNull String key) {
        return maps.containsKey(key) || external.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> get(@NonNull String key) {
        if (hasReferenceData(key)) {
            if (maps.containsKey(key)) {
                return (Collection<T>) maps.get(key);
            } else if (external.containsKey(key)) {
                ExternalDataList el = external.get(key);
            }
        }
        return null;
    }

    public static final ReferenceDataManager __instance = new ReferenceDataManager();

    public static void setup() throws ConfigurationException {
        synchronized (__instance) {
            if (__instance.state.getState() != EObjectState.Available) __instance.init();
        }
    }

    public static ReferenceDataManager get() throws StateException {
        __instance.state.check(EObjectState.Available, ReferenceDataManager.class);
        return __instance;
    }
}
