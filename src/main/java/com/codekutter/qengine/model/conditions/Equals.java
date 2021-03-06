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

package com.codekutter.qengine.model.conditions;

import com.codekutter.qengine.common.EvaluationException;
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
public class Equals<E, T> extends BaseCondition<E> {
    public static final String __OPERATION = "=";

    private final DataType.BasicDataType<T> dataType;

    public Equals(@NonNull Query<E> query, @NonNull DataType.BasicDataType<T> dataType) {
        super(query);
        this.dataType = dataType;
    }

    @Override
    public void validate() throws ValidationException {
        if (left() == null) {
            throw new ValidationException("Left Vertex not set.");
        }
        if (right() == null) {
            throw new ValidationException("Right Vertex not set.");
        }
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        return false;
    }

    @Override
    public String printString() {
        return String.format("%s %s %s", left().printString(), __OPERATION, right().printString());
    }
}
