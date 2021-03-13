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
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private static final ConnectionManager __instance = new ConnectionManager();
    private final Map<String, SessionFactory> hibernateFactory = new HashMap<>();

    private ConnectionManager() {
    }

    public static ConnectionManager get() {
        return __instance;
    }

    public ConnectionManager addHibernateSession(@NonNull String name, @NonNull SessionFactory sessionFactory) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        hibernateFactory.put(name, sessionFactory);
        return this;
    }

    public Session getHibernateSession(@NonNull String name) {
        SessionFactory sf = hibernateFactory.get(name);
        if (sf != null) {
            LogUtils.debug(getClass(), String.format("Opening new Hibernate session. [name=%s]", name));
            return sf.openSession();
        }
        return null;
    }

    public <T> HibernateDataLoader<T> getHibernateDataLoader(@NonNull String name) {
        Session session = getHibernateSession(name);
        if (session != null) {
            HibernateDataLoader<T> loader = new HibernateDataLoader<>();
            return (HibernateDataLoader<T>) loader.withConnection(session);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <C, T> QueryDataLoader<C, T> getDataLoader(@NonNull String name, @NonNull ConnectionTypes connectionType) {
        if (connectionType == ConnectionTypes.Hibernate) {
            return (QueryDataLoader<C, T>) getHibernateDataLoader(name);
        }
        return null;
    }

    public enum ConnectionTypes {
        Hibernate
    }
}
