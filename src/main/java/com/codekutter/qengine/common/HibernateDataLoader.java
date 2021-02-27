package com.codekutter.qengine.common;

import lombok.NonNull;
import org.hibernate.Session;

import java.util.Collection;

public class HibernateDataLoader<T> implements QueryDataLoader<Session, T> {
    private Session session;

    @Override
    public QueryDataLoader<Session, T> withConnection(@NonNull Session connection) {
        this.session = connection;
        return this;
    }

    @Override
    public Collection<T> read(@NonNull String query) throws DataStoreException {
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
