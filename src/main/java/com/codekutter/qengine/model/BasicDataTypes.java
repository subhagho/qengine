package com.codekutter.qengine.model;

import com.google.common.base.Preconditions;
import jdk.internal.joptsimple.internal.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum BasicDataTypes {
    Boolean("boolean"),
    Short("short"),
    Integer("integer"),
    Long("long"),
    Float("float"),
    Double("double"),
    Char("char"),
    String("string"),
    Date("date"),
    DateTime("datetime")
    ;

    private final DataType.BasicDataType dataType;

    BasicDataTypes(@NonNull String name) {
        dataType = new DataType.BasicDataType(name);
    }

    public static BasicDataTypes parse(@NonNull String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
        value = value.toLowerCase().trim();
        for(BasicDataTypes dt : BasicDataTypes.values()) {
            if (dt.dataType.name().compareTo(value) == 0) {
                return dt;
            }
        }
        return null;
    }
}
