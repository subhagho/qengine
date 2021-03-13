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
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Parameter<E, T> extends ValueDefinition<E, T> {
    public static final String __NAME = "param";

    private final String name;
    private boolean dynamic = true;

    public Parameter(@NonNull Query<E> query, @NonNull DataType.BasicDataType<T> dataType, @NonNull String name) {
        super(query, ValueType.Parameter, dataType);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
    }

    @Override
    public String printString() {
        return String.format("`%s:%s`", __NAME, name);
    }

    @Override
    public void parse(@NonNull String input) throws ValidationException {
        Preconditions.checkArgument(!jdk.internal.joptsimple.internal.Strings.isNullOrEmpty(input));
        input = input.replaceAll("\\s*", "");
        String[] parts = input.split(":");
        if (parts.length != 2) {
            throw new ValidationException(String.format("Invalid Parameter string. [string=%s]", input));
        }
        if (Strings.isNullOrEmpty(parts[0]) || parts[0].compareToIgnoreCase(__NAME) != 0) {
            throw new ValidationException(String.format("Invalid Parameter string: Parameter keyword missing. [string=%s]", input));
        }
    }
}
