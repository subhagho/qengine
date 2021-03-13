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

package com.codekutter.qengine.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;

/**
 * Exception type to be used for escalating Condition Evaluation errors.
 */
public class EvaluationException extends Exception {
    private static final String __PREFIX__ = "Evaluation Error : %s";

    /**
     * Constructor with Error message.
     *
     * @param mesg - Error Message.
     */
    public EvaluationException(String mesg) {
        super(String.format(__PREFIX__, mesg));
    }

    /**
     * Constructor with Error message and root cause.
     *
     * @param mesg  - Error Message.
     * @param cause - Cause.
     */
    public EvaluationException(String mesg, Throwable cause) {
        super(String.format(__PREFIX__, mesg), cause);
    }

    /**
     * Constructor with root cause.
     *
     * @param cause - Cause.
     */
    public EvaluationException(Throwable cause) {
        super(String.format(__PREFIX__, cause.getLocalizedMessage()), cause);
    }

    /**
     * Validate that a required property value is not NULL.
     *
     * @param property - Property name.
     * @param value    - Property value.
     * @throws EvaluationException
     */
    public static void checkNotNull(@Nonnull String property, Object value)
            throws EvaluationException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(property));
        if (value == null) {
            throw new EvaluationException(
                    String.format("Property value is NULL. [property=%s]",
                            property));
        }
    }
}
