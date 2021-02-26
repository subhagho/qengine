package com.codekutter.qengine.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class DataType {
    protected static final int RET_INCOMPATIBLE = -999;
    protected static final int RET_UNDERFLOW = -1;
    protected static final int RET_OVERFLOW = 1;

    @Setter(AccessLevel.NONE)
    private String name;

    protected DataType(@NonNull String name) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name = name;
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

    public static abstract class BasicDataType extends DataType {
        protected BasicDataType(@NonNull String name) {
            super(name);
        }
    }

    public static class DtBoolean extends BasicDataType {
        public DtBoolean() {
            super("boolean");
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
    }

    public static class DtShort extends BasicDataType {
        public DtShort() {
            super("short");
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
    }

    public static class DtInteger extends BasicDataType {
        public DtInteger() {
            super("integer");
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
    }

    public static class DtLong extends BasicDataType {
        public DtLong() {
            super("long");
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
    }

    public static class DtFloat extends BasicDataType {
        public DtFloat() {
            super("float");
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
    }

    public static class DtDouble extends BasicDataType {
        public DtDouble() {
            super("double");
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
    }

    public static class DtChar extends BasicDataType {
        public DtChar() {
            super("char");
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }
    }

    public static class DtString extends BasicDataType {
        public DtString() {
            super("string");
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }
    }

    public static class DtDate extends BasicDataType {
        public DtDate() {
            super("date");
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
    }

    public static class DtDateTime extends BasicDataType {
        public DtDateTime() {
            super("datetime");
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
    }

    public static class DtTimestamp extends BasicDataType {
        public DtTimestamp() {
            super("timestamp");
        }

        @Override
        public short compareTo(@NonNull DataType target) {
            if (equals(target)) {
                return 0;
            }
            return RET_INCOMPATIBLE;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class DtCollection extends DataType {
        private static final String __NAME = "Collection<%s>";
        private final DataType dataType;

        public DtCollection(@NonNull DataType dataType) {
            super(String.format(__NAME, dataType.name));
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
        private final BasicDataType key;
        private final DataType value;

        public DtMap(@NonNull BasicDataType key, @NonNull DataType value) {
            super(String.format(__NAME, key.name(), value.name));
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

    private static final String COLLECTION_STRING = "(\\w+)\\s*<\\s*(\\w+)\\s*>";
    private static final String MAP_STRING = "(\\w+)\\s*<\\s*(\\w+)\\s*,\\s*(\\w+)\\s*>";
    private static final Pattern COLLECTION_PATTERN = Pattern.compile(COLLECTION_STRING);
    private static final Pattern MAP_PATTERN = Pattern.compile(MAP_STRING);

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
            String vt = matcher.group(1);
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
}
