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

import lombok.NonNull;

public class Operations {
    public interface Sum<T> {
        T sum(@NonNull T[] values) throws OperationException;
    }

    public interface Subtract<T> {
        T subtract(@NonNull T source, @NonNull T value) throws OperationException;
    }

    public interface Multiply<T> {
        T multiply(@NonNull T[] values) throws OperationException;
    }

    public interface Divide<T> {
        T divide(@NonNull T numerator, @NonNull T denominator) throws OperationException;
    }

    public interface Power<T> {
        double power(@NonNull T value, @NonNull Double power) throws OperationException;
    }

    public interface Concat {
        String concat(@NonNull String[] values) throws OperationException;
    }

    public interface Substring {
        String substring(@NonNull String value, int pos, int length) throws OperationException;
    }
}
