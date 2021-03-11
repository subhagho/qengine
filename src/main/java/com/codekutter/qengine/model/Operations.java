package com.codekutter.qengine.model;

import lombok.NonNull;

public class Operations {
    public interface Sum<T> {
        T sum(@NonNull T[] values) throws OperationException;
    }

    public interface Subtract<T> {
        T subtract(@NonNull T source, @NonNull T value) throws OperationException;
    }

    public interface Multiply<T> {
        T multiply(@NonNull T[] values) throws OperationException;
    }

    public interface Divide<T> {
        T divide(@NonNull T numerator, @NonNull T denominator) throws OperationException;
    }

    public interface Power<T> {
        double power(@NonNull T value, @NonNull Double power) throws OperationException;
    }

    public interface Concat {
        String concat(@NonNull String[] values) throws OperationException;
    }

    public interface Substring {
        String substring(@NonNull String value, int pos, int length) throws OperationException;
    }
}
