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

import com.codekutter.qengine.model.DataType;
import com.codekutter.qengine.model.Query;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class ConstantCollection<E, T> extends ValueDefinition<E, T> {
    private List<T> values;

    public ConstantCollection(@NonNull Query<E> query, @NonNull DataType.BasicDataType<T> dataType) {
        super(query, ValueType.Constant, dataType);
    }

    public ConstantCollection<E, T> add(@NonNull T value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);

        return this;
    }

    public ConstantCollection<T> addAll(@NonNull Collection<T> source) {
        if (!source.isEmpty()) {
            if (values == null) {
                values = new ArrayList<>(source.size());
            }
            values.addAll(source);
        }
        return this;
    }

    public boolean isEmpty() {
        return (values == null || values.isEmpty());
    }

    public boolean contains(@NonNull T value) {
        if (values != null) {
            return values.contains(value);
        }
        return false;
    }

    public boolean contains(@NonNull T[] values) {
        boolean ret = false;
        for (T value : values) {
            ret = contains(value);
            if (!ret) break;
        }
        return ret;
    }

    public boolean contains(@NonNull Collection<T> values) {
        boolean ret = false;
        for (T value : values) {
            ret = contains(value);
            if (!ret) break;
        }
        return ret;
    }

    public T get(int index) {
        if (values != null) {
            if (index >= 0 && index < values.size()) {
                return values.get(index);
            }
        }
        return null;
    }
}
