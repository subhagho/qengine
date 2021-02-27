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
