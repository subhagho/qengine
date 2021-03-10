package com.codekutter.qengine.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestClasses {
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class TestClassInner {
        private String name;
        private int value;
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
}
