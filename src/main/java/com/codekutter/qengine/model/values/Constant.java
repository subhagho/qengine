/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * Copyright (c) 2021
 *  * Date: 13/03/21, 2:45 PM
 *  * Subho Ghosh (subho dot ghosh at outlook.com)
 */

package com.codekutter.qengine.model.values;

import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.DataType;
import com.codekutter.qengine.model.Query;
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
public class Constant<E, T> extends ValueDefinition<E, T> {
    private String value;

    public Constant(@NonNull Query<E> query, @NonNull DataType.BasicDataType<T> dataType) {
        super(query, ValueType.Constant, dataType);
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

    @Override
    public String printString() {

        return null;
    }

    @Override
    public void parse(@NonNull String input) throws ValidationException {

    }
}
