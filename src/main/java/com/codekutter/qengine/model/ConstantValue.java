package com.codekutter.qengine.model;

import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class ConstantValue extends Value {
    @Setter(AccessLevel.NONE)
    protected Object value;

    public ConstantValue(@NonNull DataType.BasicDataType<?> dataType) {
        super(ValueType.Constant);
        dataType(dataType);
    }

    public ConstantValue value(@NonNull Object value) {
        Class<?> type = value.getClass();
        Preconditions.checkArgument(Reflector.isPrimitiveTypeOrString(type));
        this.value = value;
        return this;
    }
}
