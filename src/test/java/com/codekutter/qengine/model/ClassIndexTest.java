package com.codekutter.qengine.model;

import com.codekutter.qengine.common.QueryCacheManager;
import com.codekutter.qengine.utils.LogUtils;
import com.codekutter.qengine.utils.TestClasses;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ClassIndexTest {

    @Test
    void setup() {
        try {
            ClassIndex<TestClasses.TestClassOuter> idx = QueryCacheManager.get().getClassIndex(TestClasses.TestClassOuter.class);
            LogUtils.info(getClass(), idx.toString());
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void find() {
        try {

            ClassIndex<TestClasses.TestClassOuter> idx = QueryCacheManager.get().getClassIndex(TestClasses.TestClassOuter.class);

            String path = "tc/values[key]/name";
            FieldPath fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);
            ClassIndex.IndexedField ff = idx.find(fp);

            assertNotNull(ff);
            LogUtils.info(getClass(), ff.toString());

            path = "tc/list[5]";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);
            ff = idx.find(fp);

            assertNotNull(ff);
            LogUtils.info(getClass(), ff.toString());

        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void getFieldValue() {
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
            ClassIndex<TestClasses.TestClassOuter> idx = QueryCacheManager.get().getClassIndex(TestClasses.TestClassOuter.class);

            String path = "tc/values[" + kv + "]/name";
            FieldPath fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);
            ClassIndex.IndexedField ff = idx.find(fp);

            assertNotNull(ff);
            LogUtils.info(getClass(), ff.toString());

            Object value = idx.getFieldValue(outer, fp);
            assertNotNull(value);
            LogUtils.info(getClass(), String.format("Value = %s", value));

            path = "tc/list";
            fp = new FieldPath().withPath(path, TestClasses.TestClassOuter.class);
            ff = idx.find(fp);

            assertNotNull(ff);
            LogUtils.info(getClass(), ff.toString());

            value = idx.getFieldValue(outer, fp);
            assertNotNull(value);
            LogUtils.info(getClass(), String.format("Value = %s", value));

        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }
}