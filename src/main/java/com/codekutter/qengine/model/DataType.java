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

import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class DataType {
    protected static final int RET_INCOMPATIBLE = -999;
    protected static final int RET_UNDERFLOW = -1;
    protected static final int RET_OVERFLOW = 1;
    private static final String COLLECTION_STRING = "(\\w+)\\s*<\\s*(\\w+)\\s*>";
    private static final String MAP_STRING = "(\\w+)\\s*<\\s*(\\w+)\\s*,\\s*(\\w+)\\s*>";
    private static final Pattern COLLECTION_PATTERN = Pattern.compile(COLLECTION_STRING);
    private static final Pattern MAP_PATTERN = Pattern.compile(MAP_STRING);
    @Setter(AccessLevel.NONE)
    private final String name;
    private final Class<?> type;

    protected DataType(@NonNull String name, @NonNull Class<?> type) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
        this.type = type;
    }

    public static DataType parse(@NonNull String value) throws IllegalArgumentException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
        BasicDataTypes dts = BasicDataTypes.parse(value);
        if (dts != null) {
            return dts.dataType();
        }
        Matcher matcher = MAP_PATTERN.matcher(value);
        if (matcher.matches()) {
            String kt = matcher.group(2);
            if (Strings.isNullOrEmpty(kt)) {
                throw new IllegalArgumentException(String.format("Map Key datatype not found: [type string=%s]", value));
            }
            BasicDataType bkt = BasicDataTypes.parseType(kt);
            if (bkt == null) {
                throw new IllegalArgumentException(String.format("Data Type not supported: [key type=%s]", kt));
            }
            String vt = matcher.group(3);
            if (Strings.isNullOrEmpty(vt)) {
                throw new IllegalArgumentException(String.format("Map Value datatype not found: [type string=%s]", value));
            }
            DataType vdt = parse(vt);
            if (vdt == null) {
                throw new IllegalArgumentException(String.format("Specified Map value type not found: [value type=%s]", vt));
            }
            return new DtMap(bkt, vdt);
        }
        matcher = COLLECTION_PATTERN.matcher(value);
        if (matcher.matches()) {
            String vt = matcher.group(2);
            if (Strings.isNullOrEmpty(vt)) {
                throw new IllegalArgumentException(String.format("Collection Inner Type not found: [type string=%s]", value));
            }
            BasicDataType bit = BasicDataTypes.parseType(vt);
            if (bit == null) {
                throw new IllegalArgumentException(String.format("Data Type not supported: [inner type=%s]", vt));
            }
            return new DtCollection(bit);
        }
        return null;
    }

    public static DataType convert(@NonNull Field field) {
        Class<?> type = field.getType();
        DataType dt = convert(type);
        if (dt != null) {
            return dt;
        } else if (Reflector.implementsInterface(List.class, type)) {
            Class<?> itype = Reflector.getGenericListType(field);
            DataType idt = convert(itype);
            if (idt != null) {
                return new DtCollection(idt);
            }
        } else if (Reflector.implementsInterface(Map.class, type)) {
            Class<?> ktype = Reflector.getGenericMapKeyType(field);
            DataType kdt = convert(ktype);
            if (kdt != null) {
                Class<?> vtype = Reflector.getGenericMapValueType(field);
                DataType vdt = convert(vtype);
                if (vdt == null) {
                    vdt = new DtComplex(vtype);
                }
                return new DtMap(kdt, vdt);
            }
        }
        return null;
    }

    public static DataType convert(@NonNull Class<?> type) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return BasicDataTypes.Boolean.dataType();
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return BasicDataTypes.Short.dataType();
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return BasicDataTypes.Integer.dataType();
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return BasicDataTypes.Long.dataType();
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return BasicDataTypes.Float.dataType();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return BasicDataTypes.Double.dataType();
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return BasicDataTypes.Char.dataType();
        } else if (type.equals(String.class)) {
            return BasicDataTypes.String.dataType();
        } else if (type.isEnum()) {
            return new DtEnum(type);
        } else if (type.equals(Date.class)) {
            return BasicDataTypes.DateTime.dataType();
        } else if (type.equals(java.sql.Date.class)) {
            return BasicDataTypes.Date.dataType();
        } else if (type.equals(Timestamp.class)) {
            return BasicDataTypes.Timestamp.dataType();
        }
        return null;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataType) {
            return (name.compareTo(((DataType) obj).name) == 0);
        }
        return false;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return name;
    }

    public abstract short compareTo(@NonNull DataType target);

    public static abstract class BasicDataType<T> extends DataType {
        protected BasicDataType(@NonNull String name, @NonNull Class<?> type) {
            super(name, type);
        }

        public abstract T fromString(@NonNull String value) throws ParseException;

        public abstract int compareValue(Object source, Object target) throws ParseException;

        public abstract Class<T> getJavaType();
    }

    public static class DtBoolean extends BasicDataType<Boolean> {
        public DtBoolean() {
            super("boolean", Boolean.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtShort || target instanceof DtInteger) {
                return RET_OVERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Boolean fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Boolean.parseBoolean(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Boolean sv = Reflector.parseValue(Boolean.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Boolean tv = Reflector.parseValue(Boolean.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            int rv = -1;
            if (sv == tv) {
                rv = 0;
            } else {
                rv = (sv == null || !sv ? -1 : 1);
            }
            return rv;
        }

        @Override
        public Class<Boolean> getJavaType() {
            return Boolean.class;
        }
    }

    public static class DtShort extends BasicDataType<Short>
            implements Operations.Sum<Short>,
            Operations.Subtract<Short>,
            Operations.Multiply<Short>,
            Operations.Divide<Short>,
            Operations.Power<Short> {
        public DtShort() {
            super("short", Short.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtBoolean) {
                return RET_UNDERFLOW;
            } else if (target instanceof DtInteger || target instanceof DtLong) {
                return RET_OVERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Short fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Short.parseShort(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Short sv = Reflector.parseValue(Short.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Short tv = Reflector.parseValue(Short.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return sv - tv;
        }

        @Override
        public Class<Short> getJavaType() {
            return Short.class;
        }

        @Override
        public Short sum(@NonNull Short[] values) throws OperationException {
            short sum = 0;
            for (short v : values) {
                sum += v;
            }
            return sum;
        }

        @Override
        public Short subtract(@NonNull Short source, @NonNull Short value) throws OperationException {
            return (short) (source - value);
        }

        @Override
        public Short multiply(@NonNull Short[] values) throws OperationException {
            short mul = 1;
            for (short v : values) {
                mul *= v;
            }
            return mul;
        }

        @Override
        public Short divide(@NonNull Short numerator, @NonNull Short denominator) throws OperationException {
            Preconditions.checkArgument(denominator != 0);
            return (short) (numerator / denominator);
        }

        @Override
        public double power(@NonNull Short value, @NonNull Double power) throws OperationException {
            return Math.pow(value, power);
        }
    }

    public static class DtInteger extends BasicDataType<Integer>
            implements Operations.Sum<Integer>,
            Operations.Subtract<Integer>,
            Operations.Multiply<Integer>,
            Operations.Divide<Integer>,
            Operations.Power<Integer> {
        public DtInteger() {
            super("integer", Integer.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtBoolean || target instanceof DtShort) {
                return RET_UNDERFLOW;
            } else if (target instanceof DtLong) {
                return RET_OVERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Integer fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Integer.parseInt(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Integer sv = Reflector.parseValue(Integer.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Integer tv = Reflector.parseValue(Integer.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return sv - tv;
        }

        @Override
        public Class<Integer> getJavaType() {
            return Integer.class;
        }

        @Override
        public Integer sum(@NonNull Integer[] values) throws OperationException {
            int sum = 0;
            for (int v : values) {
                sum += v;
            }
            return sum;
        }

        @Override
        public Integer subtract(@NonNull Integer source, @NonNull Integer value) throws OperationException {
            return source - value;
        }

        @Override
        public Integer multiply(@NonNull Integer[] values) throws OperationException {
            int mul = 1;
            for (int v : values) {
                mul *= v;
            }
            return mul;
        }

        @Override
        public Integer divide(@NonNull Integer numerator, @NonNull Integer denominator) throws OperationException {
            Preconditions.checkArgument(denominator != 0);
            return numerator / denominator;
        }

        @Override
        public double power(@NonNull Integer value, @NonNull Double power) throws OperationException {
            return Math.pow(value, power);
        }
    }

    public static class DtLong extends BasicDataType<Long>
            implements Operations.Sum<Long>,
            Operations.Subtract<Long>,
            Operations.Multiply<Long>,
            Operations.Divide<Long>,
            Operations.Power<Long> {
        public DtLong() {
            super("long", Long.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtBoolean || target instanceof DtShort || target instanceof DtInteger) {
                return RET_UNDERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Long fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Long.parseLong(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Long sv = Reflector.parseValue(Long.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Long tv = Reflector.parseValue(Long.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return (int) (sv - tv);
        }

        @Override
        public Class<Long> getJavaType() {
            return Long.class;
        }

        @Override
        public Long sum(@NonNull Long[] values) throws OperationException {
            long sum = 0;
            for (long v : values) {
                sum += v;
            }
            return sum;
        }

        @Override
        public Long subtract(@NonNull Long source, @NonNull Long value) throws OperationException {
            return source - value;
        }

        @Override
        public Long multiply(@NonNull Long[] values) throws OperationException {
            long mul = 1;
            for (long v : values) {
                mul *= v;
            }
            return mul;
        }

        @Override
        public Long divide(@NonNull Long numerator, @NonNull Long denominator) throws OperationException {
            Preconditions.checkArgument(denominator != 0);
            return numerator / denominator;
        }

        @Override
        public double power(@NonNull Long value, @NonNull Double power) throws OperationException {
            return Math.pow(value, power);
        }
    }

    public static class DtFloat extends BasicDataType<Float>
            implements Operations.Sum<Float>,
            Operations.Subtract<Float>,
            Operations.Multiply<Float>,
            Operations.Divide<Float>,
            Operations.Power<Float> {
        public DtFloat() {
            super("float", Float.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtBoolean || target instanceof DtShort || target instanceof DtInteger || target instanceof DtLong) {
                return RET_UNDERFLOW;
            } else if (target instanceof DtDouble) {
                return RET_OVERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Float fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Float.parseFloat(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Float sv = Reflector.parseValue(Float.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Float tv = Reflector.parseValue(Float.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return (int) (sv - tv);
        }

        @Override
        public Class<Float> getJavaType() {
            return Float.class;
        }

        @Override
        public Float sum(@NonNull Float[] values) throws OperationException {
            float sum = 0;
            for (float v : values) {
                sum += v;
            }
            return sum;
        }

        @Override
        public Float subtract(@NonNull Float source, @NonNull Float value) throws OperationException {
            return source - value;
        }

        @Override
        public Float multiply(@NonNull Float[] values) throws OperationException {
            float mul = 1;
            for (float v : values) {
                mul *= v;
            }
            return mul;
        }

        @Override
        public Float divide(@NonNull Float numerator, @NonNull Float denominator) throws OperationException {
            Preconditions.checkArgument(denominator != 0);
            return numerator / denominator;
        }

        @Override
        public double power(@NonNull Float value, @NonNull Double power) throws OperationException {
            return Math.pow(value, power);
        }
    }

    public static class DtDouble extends BasicDataType<Double>
            implements Operations.Sum<Double>,
            Operations.Subtract<Double>,
            Operations.Multiply<Double>,
            Operations.Divide<Double>,
            Operations.Power<Double> {
        public DtDouble() {
            super("double", Double.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtBoolean || target instanceof DtShort || target instanceof DtInteger || target instanceof DtLong || target instanceof DtFloat) {
                return RET_UNDERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Double fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Double.parseDouble(value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Double sv = Reflector.parseValue(Double.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Double tv = Reflector.parseValue(Double.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return (int) (sv - tv);
        }

        @Override
        public Class<Double> getJavaType() {
            return Double.class;
        }

        @Override
        public Double sum(@NonNull Double[] values) throws OperationException {
            double sum = 0;
            for (double v : values) {
                sum += v;
            }
            return sum;
        }

        @Override
        public Double subtract(@NonNull Double source, @NonNull Double value) throws OperationException {
            return source - value;
        }

        @Override
        public Double multiply(@NonNull Double[] values) throws OperationException {
            double mul = 1;
            for (double v : values) {
                mul *= v;
            }
            return mul;
        }

        @Override
        public Double divide(@NonNull Double numerator, @NonNull Double denominator) throws OperationException {
            Preconditions.checkArgument(denominator != 0);
            return numerator / denominator;
        }

        @Override
        public double power(@NonNull Double value, @NonNull Double power) throws OperationException {
            return Math.pow(value, power);
        }
    }

    public static class DtChar extends BasicDataType<Character> {
        public DtChar() {
            super("char", Character.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Character fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return value.charAt(0);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Character sv = Reflector.parseValue(Character.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Character tv = Reflector.parseValue(Character.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? 0 : sv);
            tv = (tv == null ? 0 : tv);
            return (sv - tv);
        }

        @Override
        public Class<Character> getJavaType() {
            return Character.class;
        }
    }

    public static class DtString extends BasicDataType<String>
            implements Operations.Concat, Operations.Substring {
        public DtString() {
            super("string", String.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public String fromString(@NonNull String value) throws ParseException {
            return value;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            String sv = Reflector.parseValue(String.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            String tv = Reflector.parseValue(String.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? "" : sv);
            tv = (tv == null ? "" : tv);
            return sv.compareTo(tv);
        }

        @Override
        public Class<String> getJavaType() {
            return String.class;
        }

        @Override
        public String concat(@NonNull String[] values) throws OperationException {
            String ret = values[0];
            for (int ii = 1; ii < values.length; ii++) {
                ret = ret.concat(values[ii]);
            }
            return ret;
        }

        @Override
        public String substring(@NonNull String value, int pos, int length) throws OperationException {
            return value.substring(pos, pos + length);
        }
    }

    public static class DtDate extends BasicDataType<java.sql.Date> {
        public DtDate() {
            super("date", java.sql.Date.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtDateTime) {
                return RET_OVERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public java.sql.Date fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                SimpleDateFormat format = new SimpleDateFormat();
                Date parsed = format.parse(value);
                return new java.sql.Date(parsed.getTime());
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            java.sql.Date sv = Reflector.parseValue(java.sql.Date.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            java.sql.Date tv = Reflector.parseValue(java.sql.Date.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? new java.sql.Date(0) : sv);
            tv = (tv == null ? new java.sql.Date(0) : tv);
            return sv.compareTo(tv);
        }

        @Override
        public Class<java.sql.Date> getJavaType() {
            return java.sql.Date.class;
        }
    }

    public static class DtDateTime extends BasicDataType<Date> {
        public DtDateTime() {
            super("datetime", Date.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            } else if (target instanceof DtDate) {
                return RET_UNDERFLOW;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Date fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                SimpleDateFormat format = new SimpleDateFormat();
                return format.parse(value);
            }
            return null;
        }


        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Date sv = Reflector.parseValue(Date.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Date tv = Reflector.parseValue(Date.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? new Date(0) : sv);
            tv = (tv == null ? new Date(0) : tv);
            return sv.compareTo(tv);
        }

        @Override
        public Class<Date> getJavaType() {
            return Date.class;
        }
    }

    public static class DtTimestamp extends BasicDataType<Timestamp> {
        public DtTimestamp() {
            super("timestamp", Timestamp.class);
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Timestamp fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Timestamp.valueOf(value);
            }
            return null;
        }


        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            Timestamp sv = Reflector.parseValue(Timestamp.class, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Timestamp tv = Reflector.parseValue(Timestamp.class, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }
            sv = (sv == null ? new Timestamp(0) : sv);
            tv = (tv == null ? new Timestamp(0) : tv);
            return sv.compareTo(tv);
        }

        @Override
        public Class<Timestamp> getJavaType() {
            return Timestamp.class;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class DtCollection extends DataType {
        private static final String __NAME = "Collection<%s>";
        private final DataType dataType;

        public DtCollection(@NonNull DataType dataType) {
            super(String.format(__NAME, dataType.name), Collection.class);
            this.dataType = dataType;
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (target instanceof DtCollection) {
                DtCollection tc = (DtCollection) target;
                if (dataType.equals(tc.dataType)) {
                    return 0;
                }
            }
            return RET_INCOMPATIBLE;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class DtMap extends DataType {
        private static final String __NAME = "Map<%s, %s>";
        private final DataType key;
        private final DataType value;

        public DtMap(@NonNull DataType key, @NonNull DataType value) {
            super(String.format(__NAME, key.name(), value.name), Map.class);
            this.key = key;
            this.value = value;
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (target instanceof DtMap) {
                if (key.equals(((DtMap) target).key)) {
                    if (value.equals(((DtMap) target).value)) {
                        return 0;
                    }
                }
            }
            return RET_INCOMPATIBLE;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class DtEnum<T extends Enum<T>> extends BasicDataType<Enum<T>> {
        private final Class<T> type;

        protected DtEnum(@NonNull Class<T> type) {
            super(type.getCanonicalName(), Enum.class);
            this.type = type;
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (target instanceof DtEnum) {
                if (name().compareTo(target.name) == 0) {
                    return 0;
                }
            }
            return RET_INCOMPATIBLE;
        }

        @Override
        public Enum<T> fromString(@NonNull String value) throws ParseException {
            if (!Strings.isNullOrEmpty(value)) {
                return Reflector.parseValue(type, value);
            }
            return null;
        }

        @Override
        public int compareValue(Object source, Object target) throws ParseException {
            if (source != null && target != null) {
                Preconditions.checkArgument(source.getClass().equals(target.getClass()));
            }
            Enum<T> sv = Reflector.parseValue(type, source);
            if (source != null && sv == null) {
                throw new ParseException(String.format("Invalid Value type (source): type=%s", source.getClass().getCanonicalName()), 0);
            }
            Enum<T> tv = Reflector.parseValue(type, target);
            if (target != null && tv == null) {
                throw new ParseException(String.format("Invalid Value type (target): type=%s", target.getClass().getCanonicalName()), 0);
            }

            int osv = (sv == null ? 0 : sv.ordinal());
            int otv = (tv == null ? 0 : tv.ordinal());
            return osv - otv;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<Enum<T>> getJavaType() {
            return (Class<Enum<T>>) type;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class DtComplex extends DataType {
        private final Class<?> type;

        public DtComplex(@NonNull Class<?> type) {
            super(type.getCanonicalName(), Object.class);
            this.type = type;
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (target instanceof DtComplex) {
                if (name().compareTo(target.name) == 0) {
                    return 0;
                }
            }
            return RET_INCOMPATIBLE;
        }
    }
}
