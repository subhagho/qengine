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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class FieldAlias<E, T> extends Field<E, T> {
    public static final String __NAME = "alias";
    private String name;

    public FieldAlias(@NonNull Query<E> query, @NonNull Class<?> entityType, DataType.@NonNull BasicDataType<T> dataType) {
        super(query, entityType, dataType);
    }

    @Override
    public String printString() {
        return String.format("%s:%s", __NAME, name);
    }

    @Override
    public void parse(@NonNull String input) throws ValidationException {
        super.parse(input);
    }
}
