package com.codekutter.qengine.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Field extends Vertex {
    @Setter(AccessLevel.NONE)
    private final Class<?> entityType;
    private final DataType dataType;
    private String name;
    private FieldPath path;

    public Field(@NonNull Class<?> entityType, @NonNull DataType dataType) {
        this.entityType = entityType;
        this.dataType = dataType;
    }

    public boolean isValid(String path) {
        return false;
    }
}
