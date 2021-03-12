package com.codekutter.qengine.model.values;

import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.DataType;
import com.codekutter.qengine.utils.Reflector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Field extends Value {
    @Setter(AccessLevel.NONE)
    private final Class<?> entityType;
    @Setter(AccessLevel.NONE)
    private String name;
    @Setter(AccessLevel.NONE)
    private FieldPath path;

    public Field(@NonNull Class<?> entityType, @NonNull DataType dataType) {
        super(ValueType.Field, dataType);
        this.entityType = entityType;
    }

    public Field withPath(@NonNull String path) throws IllegalArgumentException {
        this.path = new FieldPath();
        this.path.withPath(path, entityType);

        return this;
    }

    public Object value(@NonNull Object source) throws ValidationException {
        if (!Reflector.isSuperType(entityType, source.getClass())) {
            throw new ValidationException(String.format("Value source expected to be of type [%s]", entityType.getCanonicalName()));
        }

        return null;
    }
}
