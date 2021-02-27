package com.codekutter.qengine.common;

import lombok.NonNull;

import java.io.Closeable;
import java.util.Collection;

public interface QueryDataLoader<C, T> extends Closeable {
    QueryDataLoader<C, T> withConnection(@NonNull C connection);

    Collection<T> read(@NonNull String query, @NonNull Class<T> type, Object... params) throws DataStoreException;

    Collection<T> read(@NonNull String query, @NonNull Class<T> type, Collection<?> params) throws DataStoreException;
}
