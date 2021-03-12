package com.codekutter.qengine.model.values;

import com.codekutter.qengine.model.DataType;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Accessors(fluent = true)
public class Constant extends Value {
    private String value;

    public Constant(@NonNull DataType dataType) {
        super(ValueType.Constant, dataType);
    }

    public boolean getBooleanValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Boolean.parseBoolean(value);
    }

    public short getShortValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Short.parseShort(value);
    }

    public int getIntegerValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Integer.parseInt(value);
    }

    public long getLongValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Long.parseLong(value);
    }

    public float getFloatValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Float.parseFloat(value);
    }

    public double getDoubleValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        return Double.parseDouble(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object getEnumValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        if (!(dataType() instanceof DataType.DtEnum)) {
            throw new ParseException(String.format("Invalid DataType. [type=%s]", dataType().name()), 0);
        }
        Class<Enum> et = (Class<Enum>) ((DataType.DtEnum) dataType()).type();
        return Enum.valueOf(et, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object getEnumValue(@NonNull Class<?> type) throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        Class<Enum> et = (Class<Enum>) type;
        return Enum.valueOf(et, value);
    }

    public Date getDateValue() throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        DateFormat fmt = new SimpleDateFormat();
        return fmt.parse(value);
    }

    public Date getDateValue(@NonNull String format) throws ParseException {
        if (Strings.isNullOrEmpty(value)) {
            throw new ParseException("Value is NULL/Empty.", 0);
        }
        DateFormat fmt = new SimpleDateFormat(format);
        return fmt.parse(value);
    }

    public long getTimestampValue() throws ParseException {
        return getLongValue();
    }
}
