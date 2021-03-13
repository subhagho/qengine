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

import com.codekutter.qengine.model.ClassIndex;

import java.util.HashMap;
import java.util.Map;

public class QueryCacheManager {
    private static final QueryCacheManager __instance = new QueryCacheManager();
    private final Map<Class<?>, ClassIndex<?>> classIndex = new HashMap<>();

    public static QueryCacheManager get() {
        return __instance;
    }

    @SuppressWarnings("unchecked")
    public <T> ClassIndex<T> getClassIndex(Class<T> type) {
        if (!classIndex.containsKey(type)) {
            ClassIndex<T> index = new ClassIndex<>(type);
            index.setup();
            classIndex.put(type, index);
        }
        return (ClassIndex<T>) classIndex.get(type);
    }
}
