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

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.conditions.BaseCondition;
import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(fluent = true)
public class Query<T> {
    private String name;
    private final Class<T> type;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<String, String> parameters;
    private BaseCondition condition;

    public Query(@NonNull Class<T> type) {
        this.type = type;
    }

    public Query<T> addParameter(@NonNull String key, String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        return this;
    }

    public String getParameter(@NonNull String key) {
        if (parameters != null) {
            return parameters.get(key);
        }
        return null;
    }

    public void validate() throws ValidationException {
        if (condition == null) {
            throw new ValidationException("Query condition not set.");
        }
        condition.validate();
    }

    public boolean evaluate(@NonNull Object value) throws EvaluationException {
        try {
            validate();
            if (!Reflector.isSuperType(type, value.getClass())) {
                throw new EvaluationException(String.format("Type Mismatch: [expected=%s][type=%s]", type.getCanonicalName(), value.getClass().getCanonicalName()));
            }
            return condition.evaluate(value);
        } catch (Exception ex) {
            throw new EvaluationException(ex);
        }
    }
}
