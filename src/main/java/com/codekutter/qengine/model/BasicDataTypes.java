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
    Timestamp(new DataType.DtTimestamp());

    private final DataType.BasicDataType dataType;

    BasicDataTypes(@NonNull DataType.BasicDataType dataType) {
        this.dataType = dataType;
    }

    public static BasicDataTypes parse(@NonNull String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
        value = value.toLowerCase().trim();
        for (BasicDataTypes dt : BasicDataTypes.values()) {
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
