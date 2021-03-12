package com.codekutter.qengine.model.values;

import com.codekutter.qengine.model.DataType;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Parameter extends Value {
    private final String name;

    public Parameter(@NonNull DataType dataType, @NonNull String name) {
        super(ValueType.Parameter, dataType);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
    }
}
