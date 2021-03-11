package com.codekutter.qengine.model;

import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.List;

public class ConstCollectionValue extends ConstantValue {
    public ConstCollectionValue(DataType.@NonNull BasicDataType<?> dataType) {
        super(dataType);
    }

    @Override
    public ConstantValue value(@NonNull Object value) {
        Class<?> type = value.getClass();
        Preconditions.checkArgument(Reflector.implementsInterface(List.class, type));
        this.value = value;
        return this;
    }

}
