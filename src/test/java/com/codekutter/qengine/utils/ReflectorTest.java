package com.codekutter.qengine.utils;

import com.codekutter.qengine.model.FieldPath;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {

    @Test
    void getNestedFieldValue() {
        try {
            TestClasses.TestClassOuter outer = new TestClasses.TestClassOuter();
            outer.dv(((double) System.currentTimeMillis()) / 3.142);
            outer.sv( UUID.randomUUID().toString());

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
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "sv";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "tc/list[5]";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "tc/values[ " + kv + "]/name";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }
}