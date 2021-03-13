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

package com.codekutter.qengine.utils;

import com.codekutter.qengine.model.values.FieldPath;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ReflectorTest {

    @Test
    void getNestedFieldValue() {
        try {
            TestClasses.TestClassOuter outer = new TestClasses.TestClassOuter();
            outer.dv(((double) System.currentTimeMillis()) / 3.142);
            outer.sv(UUID.randomUUID().toString());

            String kv = "";

            for (int ii = 0; ii < 10; ii++) {
                String k = UUID.randomUUID().toString();
                outer.tc().getList().add(k);
                TestClasses.TestClassInner ti = new TestClasses.TestClassInner();
                ti.name(k);
                outer.tc().getValues().put(k, ti);
                if (ii == 7) {
                    kv = k;
                }
            }
            String path = "dv";
            FieldPath fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            Object v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof Double);
            LogUtils.info(getClass(), String.format("%s=%s", path, v));

            path = "sv";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, v));

            path = "tc/list[5]";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, v));

            path = "tc/values[ " + kv + "]/name";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, v));

        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }
}