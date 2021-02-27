package com.codekutter.qengine.model;

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
        Query
    }

    @Setter(AccessLevel.NONE)
    private final ValueType type;
    private DataType dataType;

    public Value(@NonNull ValueType type) {
        this.type = type;
    }
}
