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
}