package com.codekutter.qengine.model;

import com.codekutter.qengine.utils.LogUtils;
import com.codekutter.qengine.utils.TestClasses;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassIndexTest {

    @Test
    void setup() {
        try {
            ClassIndex<TestClasses.TestClassOuter> idx = new ClassIndex<>(TestClasses.TestClassOuter.class);
            idx.setup();
            LogUtils.info(getClass(), idx.toString());
        } catch (Throwable t) {
            LogUtils.error(getClass(), t);
            fail(t);
        }
    }

    @Test
    void find() {
        try {
            ClassIndex<TestClasses.TestClassOuter> idx = new ClassIndex<>(TestClasses.TestClassOuter.class);
            idx.setup();

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
}