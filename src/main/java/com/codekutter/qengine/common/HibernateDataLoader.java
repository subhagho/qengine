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

import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

public class HibernateDataLoader<T> implements QueryDataLoader<Session, T> {
    private Session session;

    @Override
    public QueryDataLoader<Session, T> withConnection(@NonNull Session connection) {
        this.session = connection;
        return this;
    }

    @Override
    public Collection<T> read(@NonNull String query, @NonNull Class<T> type, Object... params) throws DataStoreException {
        if (params == null || params.length <= 0) {
            return execute(query, type);
        } else {
            return execute(query, type, params);
        }
    }

    @Override
    public Collection<T> read(@NonNull String query, @NonNull Class<T> type, Collection<Object> params) throws DataStoreException {
        if (params == null || params.isEmpty()) {
            return execute(query, type);
        } else {
            return execute(query, type, params);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<T> execute(String query, Class<T> type) throws DataStoreException {
        Query qq = session.createQuery(query, type);
        List<T> result = qq.getResultList();
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Collection<T> execute(@NonNull String query, @NonNull Class<T> type, Object... params) throws DataStoreException {
        Query qq = session.createQuery(query, type);
        for (int ii = 0; ii < params.length; ii++) {
            qq.setParameter(ii, params[ii]);
        }
        List<T> result = qq.getResultList();
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Collection<T> execute(@NonNull String query, @NonNull Class<T> type, Collection<Object> params) throws DataStoreException {
        Query qq = session.createQuery(query, type);
        int ii = 0;
        for (Object param : params) {
            qq.setParameter(ii, param);
            ii++;
        }
        List<T> result = qq.getResultList();
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return null;
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
            session = null;
        }
    }
}
