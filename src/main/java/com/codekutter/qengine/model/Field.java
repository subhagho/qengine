package com.codekutter.qengine.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Field extends Vertex {
    @Setter(AccessLevel.NONE)
    private final Class<?> entityType;
    private final DataType dataType;
    @Setter(AccessLevel.NONE)
    private String name;
    @Setter(AccessLevel.NONE)
    private FieldPath path;

    public Field(@NonNull Class<?> entityType, @NonNull DataType dataType) {
        this.entityType = entityType;
        this.dataType = dataType;
    }

    public Field withPath(@NonNull String path) throws IllegalArgumentException {
        this.path = new FieldPath();
        this.path.withPath(path, entityType);

        return this;
    }
}
