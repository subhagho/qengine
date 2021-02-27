package com.codekutter.qengine.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum BasicDataTypes {
    Boolean(new DataType.DtBoolean()),
    Short(new DataType.DtShort()),
    Integer(new DataType.DtInteger()),
    Long(new DataType.DtLong()),
    Float(new DataType.DtFloat()),
    Double(new DataType.DtDouble()),
    Char(new DataType.DtChar()),
    String(new DataType.DtString()),
    Date(new DataType.DtDate()),
    DateTime(new DataType.DtDateTime()),
    Timestamp(new DataType.DtTimestamp())
    ;

    private final DataType.BasicDataType dataType;

    BasicDataTypes(@NonNull DataType.BasicDataType dataType) {
        this.dataType = dataType;
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

    public static DataType.BasicDataType parseType(@NonNull String value) {
        BasicDataTypes bt = parse(value);
        if (bt != null) {
            return bt.dataType;
        }
        return null;
    }
}
