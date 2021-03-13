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

import com.codekutter.qengine.utils.LogUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DataTypeTest {
    @Test
    void parse() {
        try {
            String cparam = "List <integer>";
            String mparam = "Map <double, string>";

            DataType cdt = DataType.parse(cparam);
            assertNotNull(cdt);
            assertTrue(cdt instanceof DataType.DtCollection);

            DataType mdt = DataType.parse(mparam);
            assertNotNull(mdt);
            assertTrue(mdt instanceof DataType.DtMap);
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void convert() {
        try {
            Field[] fields = TestClass.class.getFields();
            for (Field f : fields) {
                DataType dt = DataType.convert(f);
                assertNotNull(dt);
                LogUtils.debug(getClass(), String.format("[field=%s][type=%s]", f.getType().getCanonicalName(), dt.name()));
            }
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void testConvert() {
        try {
            DataType dt = DataType.convert(Timestamp.class);
            assertNotNull(dt);
            assertEquals(dt.name(), BasicDataTypes.Timestamp.dataType().name());

            dt = DataType.convert(Date.class);
            assertNotNull(dt);
            assertEquals(dt.name(), BasicDataTypes.DateTime.dataType().name());

            dt = DataType.convert(java.sql.Date.class);
            assertNotNull(dt);
            assertEquals(dt.name(), BasicDataTypes.Date.dataType().name());
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void compareValue() {
        try {
            DataType.DtEnum<TestEnum> dt = new DataType.DtEnum<>(TestEnum.class);
            int ret = dt.compareValue(TestEnum.ONE, TestEnum.THREE);
            assertTrue(ret < 0);
            ret = dt.compareValue(TestEnum.TWO, TestEnum.TWO);
            assertEquals(0, ret);
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    public enum TestEnum {
        ONE, TWO, THREE
    }

    @Getter
    @Setter
    public static class TestClass {
        private int id;
        private double value;
        private String name;
        private TestEnum evalue;
        private Date date;
    }
}