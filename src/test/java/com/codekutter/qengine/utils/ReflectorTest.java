package com.codekutter.qengine.utils;

import com.codekutter.qengine.model.FieldPath;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class TestClassInner {
        private String name;
    }

    @Getter
    @Setter
    public static class TestClass {
        private Map<String, TestClassInner> values = new HashMap<>();
        private List<String> list = new ArrayList<>();
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class TestClassOuter {
        private double dv;
        private String sv;
        private TestClass tc = new TestClass();
    }

    @Test
    void getNestedFieldValue() {
        try {
            TestClassOuter outer = new TestClassOuter();
            outer.dv = ((double) System.currentTimeMillis()) / 3.142;
            outer.sv = UUID.randomUUID().toString();

            String kv = "";

            for (int ii = 0; ii < 10; ii++) {
                String k = UUID.randomUUID().toString();
                outer.tc.list.add(k);
                TestClassInner ti = new TestClassInner();
                ti.name = k;
                outer.tc.values.put(k, ti);
                if (ii == 7) {
                    kv = k;
                }
            }
            String path = "dv";
            FieldPath fp = new FieldPath().withPath(path, TestClassOuter.class);

            Object v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof Double);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "sv";
            fp = new FieldPath().withPath(path, TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "tc/list[5]";
            fp = new FieldPath().withPath(path, TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

            path = "tc/values[ " + kv + "]/name";
            fp = new FieldPath().withPath(path, TestClassOuter.class);

            v = Reflector.getNestedFieldValue(outer, fp);
            assertTrue(v instanceof String);
            LogUtils.info(getClass(), String.format("%s=%s", path, String.valueOf(v)));

        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }
}