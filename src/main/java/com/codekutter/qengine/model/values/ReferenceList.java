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

import com.codekutter.qengine.common.ReferenceDataManager;
import com.codekutter.qengine.common.StateException;
import com.codekutter.qengine.model.DataType;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Collection;

@Getter
@Accessors(fluent = true)
public class ReferenceList<T> extends ValueDefinition<T> {
    private final String name;

    public ReferenceList(@NonNull DataType.BasicDataType<T> dataType, @NonNull String name) {
        super(ValueType.Reference, dataType);
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public Collection<T> getReferenceList(@NonNull Class<T> type) throws ClassCastException, StateException {
        Preconditions.checkArgument(dataType() != null);
        DataType dt = DataType.convert(type);
        if (dt == null) {
            throw new ClassCastException(String.format("Expected=%s, Passed=%s", dataType().name(), type.getCanonicalName()));
        }
        if (!dataType().equals(dt)) {
            throw new ClassCastException(String.format("Expected=%s, Passed=%s", dataType().name(), dt.name()));
        }
        return (Collection<T>) ReferenceDataManager.get();
    }
}
