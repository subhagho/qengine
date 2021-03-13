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
import com.codekutter.qengine.model.BooleanVertex;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(fluent = true)
public class Or extends BaseCondition {

    @Override
    public void validate() throws ValidationException {
        if (left() instanceof BooleanVertex) {
            throw new ValidationException("Left condition missing or invalid.");
        }
        ((BooleanVertex) left()).validate();
        if (right() instanceof BooleanVertex) {
            throw new ValidationException("Right condition missing or invalid");
        }
        ((BooleanVertex) right()).validate();
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        try {
            validate();
            return (((BooleanVertex) left()).evaluate(data) || ((BooleanVertex) right()).evaluate(data));
        } catch (EvaluationException e) {
            throw e;
        } catch (Throwable t) {
            throw new EvaluationException(t);
        }
    }
}
