/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * Copyright (c) 2021
 *  * Date: 13/03/21, 2:45 PM
 *  * Subho Ghosh (subho dot ghosh at outlook.com)
 */

package com.codekutter.qengine.common;

import com.codekutter.qengine.utils.LogUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ReferenceDataManager {
    public static final ReferenceDataManager __instance = new ReferenceDataManager();
    private static final String CACHE_EXTERNAL_DATA = "cache.data.external";
    private final ObjectState state = new ObjectState();
    private final Map<String, Collection<?>> maps = new HashMap<>();
    private final Map<String, ExternalDataList> external = new HashMap<>();
    private CacheManager cacheManager;
    private final ReentrantLock edcLock = new ReentrantLock();

    private ReferenceDataManager() {
    }

    public static void setup() throws ConfigurationException {
        synchronized (__instance) {
            if (__instance.state.getState() != EObjectState.Available) __instance.init();
        }
    }

    public static ReferenceDataManager get() throws StateException {
        __instance.state.check(EObjectState.Available, ReferenceDataManager.class);
        return __instance;
    }

    private void init() throws ConfigurationException {
        try {
            state.setState(EObjectState.Available);
        } catch (Throwable t) {
            state.setError(t);
            LogUtils.error(getClass(), t);
            throw new ConfigurationException(t);
        }
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
    public <T> Collection<T> get(@NonNull String key) throws DataStoreException {
        if (hasReferenceData(key)) {
            if (maps.containsKey(key)) {
                return (Collection<T>) maps.get(key);
            } else if (external.containsKey(key)) {
                ExternalDataList el = external.get(key);
                if (el != null) return get(el);
            }
        }
        return null;
    }

    private <C, T> Collection<T> get(ExternalDataList dataList) throws DataStoreException {
        Collection<T> data = checkCache(dataList);
        if (data == null) {
            return fetchFromDataStore(dataList);
        }
        return data;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> Collection<T> checkCache(ExternalDataList el) throws DataStoreException {
        if (cacheManager != null && el.cached()) {
            Cache<String, Collection> cache = cacheManager.getCache(CACHE_EXTERNAL_DATA, String.class, Collection.class);
            if (cache != null) {
                Collection<T> values = cache.get(el.name());
                return values;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <C, T> Collection<T> fetchFromDataStore(ExternalDataList el) throws DataStoreException {
        edcLock.lock();
        try {
            Collection<T> data = checkCache(el);
            if (data != null) return data;

            QueryDataLoader<C, T> loader = ConnectionManager.get().getDataLoader(el.connection(), el.connectionType());
            if (loader == null) {
                throw new DataStoreException(String.format("Data Store connection not found: [name=%s][type=%s]", el.connection(), el.connectionType().name()));
            }
            data = loader.read(el.query(), (Class<T>) el.dataType().type(), el.params());
            if (data != null && cacheManager != null && el.cached()) {
                Cache<String, Collection> cache = cacheManager.getCache(CACHE_EXTERNAL_DATA, String.class, Collection.class);
                if (cache != null) {
                    cache.put(el.name(), data);
                } else {
                    LogUtils.warn(getClass(), String.format("External Data Cache not setup. [name=%s]", CACHE_EXTERNAL_DATA));
                }
            }
            return data;
        } finally {
            edcLock.unlock();
        }
    }
}
