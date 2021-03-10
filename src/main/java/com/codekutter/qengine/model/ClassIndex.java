package com.codekutter.qengine.model;

import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
public class ClassIndex<T> {
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class IndexedField {
        private Field field;
        private Method getter;
        private Method setter;

        @Override
        public String toString() {
            return "IndexedField{" +
                    "field={" + field.getName() + ", type=" + field.getType().getCanonicalName() + "}\n" +
                    ", getter=" + getter.getName() +
                    ", setter=" + (setter != null ? setter.getName() : "none") +
                    '}';
        }
    }

    private final Class<T> type;
    private final Map<String, IndexedField> index;

    public ClassIndex(Class<T> type) {
        this.type = type;
        index = new HashMap<>();
    }

    public void setup() {
        Preconditions.checkArgument(type != null);
        try {
            setup(type, null);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private void setup(Class<?> t, String path) throws Exception {
        Field[] fields = Reflector.getAllFields(t);
        if (fields != null && fields.length > 0) {
            for (Field f : fields) {
                Method getter = Reflector.getAccessor(t, f);
                if (getter == null) continue;
                Method setter = Reflector.getSetter(t, f);

                String p;
                if (Strings.isNullOrEmpty(path)) {
                    p = f.getName();
                } else {
                    p = String.format("%s/%s", path, f.getName());
                }
                if (!Reflector.isPrimitiveTypeOrString(f.getType())
                        && f.getType() != Class.class && f.getType() != Object.class) {
                    if (Reflector.implementsInterface(List.class, f.getType())) {
                        Class<?> it = Reflector.getGenericListType(f);
                        setup(it, p);
                    } else if (Reflector.implementsInterface(Set.class, f.getType())) {
                        Class<?> it = Reflector.getGenericSetType(f);
                        setup(it, p);
                    } else if (Reflector.implementsInterface(Map.class, f.getType())) {
                        Class<?> it = Reflector.getGenericMapValueType(f);
                        setup(it, p);
                    } else
                        setup(f.getType(), p);
                }
                IndexedField ff = new IndexedField();
                ff.field = f;
                ff.getter = getter;
                ff.setter = setter;

                index.put(p, ff);
            }
        }
    }

    public IndexedField find(@NonNull FieldPath path) {
        String p = getPath(path);
        if (index.containsKey(p)) {
            return index.get(p);
        }
        return null;
    }

    public IndexedField find(@NonNull String path) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path));
        return index.get(path);
    }

    public String getPath(@NonNull FieldPath path) {
        StringBuffer buffer = new StringBuffer();
        FieldPath.PathNode[] paths = path.getNodes();
        if (paths != null && paths.length > 0) {
            for (FieldPath.PathNode pn : paths) {
                if (buffer.length() != 0) {
                    buffer.append("/");
                }
                buffer.append(pn.name());
            }
        }
        return buffer.toString();
    }

    public Object getFieldValue(@NonNull Object source, @NonNull FieldPath path) {
        Preconditions.checkArgument(index.size() > 0);

        return null;
    }

    public FieldPath parse(@NonNull String path) throws ValidationException {
        try {
            return new FieldPath().withPath(path, this);
        } catch (Throwable t) {
            throw new ValidationException(String.format("Error parsing path [%s].", path), t);
        }
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
        StringBuffer buffer = new StringBuffer();
        buffer.append("Type=[").append(type.getCanonicalName()).append("]\n");
        buffer.append("Index={\n");
        for (String k : index.keySet()) {
            IndexedField ff = index.get(k);
            buffer.append("Key=[").append(k).append("],");
            buffer.append("Field=[").append(ff.toString()).append("]\n");
        }
        buffer.append("}\n");
        return buffer.toString();
    }
}
