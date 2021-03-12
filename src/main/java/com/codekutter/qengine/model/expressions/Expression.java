package com.codekutter.qengine.model.expressions;

import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.DataType;
import com.codekutter.qengine.model.Vertex;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class Expression<T> extends Vertex {
    private final DataType.BasicDataType<T> dataType;

    public Expression(@NonNull DataType.BasicDataType<T> dataType) {
        this.dataType = dataType;
    }

    public abstract T evaluate(@NonNull Object value) throws ValidationException;
}
