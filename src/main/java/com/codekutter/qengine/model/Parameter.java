package com.codekutter.qengine.model;

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

    public Parameter(@NonNull String name) {
        super(ValueType.Parameter);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
    }
}
