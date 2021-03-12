package com.codekutter.qengine.model.values;

import com.codekutter.qengine.model.DataType;
import com.codekutter.qengine.model.Vertex;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class Value extends Vertex {
    public enum ValueType {
        Constant,
        Parameter,
        Reference,
        Query,
        Field
    }

    @Setter(AccessLevel.NONE)
    private final ValueType type;
    private final DataType dataType;

    public Value(@NonNull ValueType type, @NonNull DataType dataType) {
        this.type = type;
        this.dataType = dataType;
    }
}
