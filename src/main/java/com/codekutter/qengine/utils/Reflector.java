package com.codekutter.qengine.utils;

import com.codekutter.qengine.model.FieldPath;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Reflector {

    /**
     * Find the field with the specified name in this type or a parent type.
     *
     * @param type - Class to find the field in.
     * @param name - Field name.
     * @return - Found Field or NULL
     */
    public static Field findField(@Nonnull Class<?> type,
                                  @Nonnull String name) {
        Preconditions.checkArgument(type != null);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        if (name.indexOf('.') > 0) {
            String[] parts = name.split("\\.");
            int indx = 0;
            Class<?> ct = type;
            Field f = null;
            while (indx < parts.length) {
                f = findField(ct, parts[indx]);
                if (f == null) break;
                ct = f.getType();
                if (implementsInterface(List.class, ct)) {
                    ct = getGenericListType(f);
                } else if (implementsInterface(Set.class, ct)) {
                    ct = getGenericSetType(f);
                }
                indx++;
            }
            return f;
        } else {
            Field[] fields = type.getDeclaredFields();
            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    if (field.getName().compareTo(name) == 0) {
                        return field;
                    }
                }
            }
            Class<?> parent = type.getSuperclass();
            if (parent != null && !parent.equals(Object.class)) {
                return findField(parent, name);
            }
        }
        return null;
    }


    /**
     * Get the Parameterized type of the List field specified.
     *
     * @param field - Field to extract the Parameterized type for.
     * @return - Parameterized type.
     */
    public static Class<?> getGenericListType(@Nonnull Field field) {
        Preconditions.checkArgument(field != null);
        Preconditions
                .checkArgument(implementsInterface(List.class, field.getType()));

        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        return (Class<?>) ptype.getActualTypeArguments()[0];
    }

    /**
     * Get the Parameterized type of the Set field specified.
     *
     * @param field - Field to extract the Parameterized type for.
     * @return - Parameterized type.
     */
    public static Class<?> getGenericSetType(@Nonnull Field field) {
        Preconditions.checkArgument(field != null);
        Preconditions
                .checkArgument(implementsInterface(Set.class, field.getType()));

        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        return (Class<?>) ptype.getActualTypeArguments()[0];
    }

    /**
     * Check is the passed type (or its ancestor) implements the specified interface.
     *
     * @param intf - Interface type to check.
     * @param type - Type implementing expected interface.
     * @return - Implements Interface?
     */
    public static boolean implementsInterface(@Nonnull Class<?> intf,
                                              @Nonnull Class<?> type) {
        Preconditions.checkArgument(intf != null);
        Preconditions.checkArgument(type != null);

        if (intf.equals(type)) {
            return true;
        }
        Class<?>[] intfs = type.getInterfaces();
        if (intfs != null && intfs.length > 0) {
            for (Class<?> itf : intfs) {
                if (isSuperType(intf, itf)) {
                    return true;
                }
            }
        }
        Class<?> parent = type.getSuperclass();
        if (parent != null && !parent.equals(Object.class)) {
            return implementsInterface(intf, parent);
        }
        return false;
    }

    /**
     * Get the Parameterized type of the Map key field specified.
     *
     * @param field - Field to extract the Parameterized type for.
     * @return - Parameterized type.
     */
    public static Class<?> getGenericMapKeyType(@Nonnull Field field) {
        Preconditions
                .checkArgument(implementsInterface(Map.class, field.getType()));

        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        return (Class<?>) ptype.getActualTypeArguments()[0];
    }

    /**
     * Get the Parameterized type of the Map value field specified.
     *
     * @param field - Field to extract the Parameterized type for.
     * @return - Parameterized type.
     */
    public static Class<?> getGenericMapValueType(@Nonnull Field field) {
        Preconditions
                .checkArgument(implementsInterface(Map.class, field.getType()));

        ParameterizedType ptype = (ParameterizedType) field.getGenericType();
        return (Class<?>) ptype.getActualTypeArguments()[1];
    }


    /**
     * Check if the parent type specified is an ancestor (inheritance) of the passed type.
     *
     * @param parent - Ancestor type to check.
     * @param type   - Inherited type
     * @return - Is Ancestor type?
     */
    public static boolean isSuperType(@Nonnull Class<?> parent,
                                      @Nonnull Class<?> type) {
        Preconditions.checkArgument(parent != null);
        Preconditions.checkArgument(type != null);
        if (parent.equals(type)) {
            return true;
        } else if (type.equals(Object.class)) {
            return false;
        } else {
            Class<?> pp = type.getSuperclass();
            if (pp == null) {
                return false;
            }
            return isSuperType(parent, pp);
        }
    }

    public static boolean isNumericType(@Nonnull Class<?> type) {
        if (type.isPrimitive())
            return true;
        else return type.equals(Boolean.class) || type.equals(boolean.class) ||
                type.equals(Short.class) || type.equals(short.class)
                || type.equals(Integer.class) || type.equals(int.class) ||
                type.equals(Long.class) || type.equals(long.class)
                || type.equals(Float.class) || type.equals(float.class) ||
                type.equals(Double.class) || type.equals(double.class)
                || type.equals(Character.class) || type.equals(char.class);
    }

    /**
     * Check if the specified type is a primitive or primitive type class.
     *
     * @param type - Field to check primitive for.
     * @return - Is primitive?
     */
    public static boolean isPrimitiveTypeOrClass(@Nonnull Class<?> type) {
        if (isNumericType(type)) return true;
        return type.equals(Class.class);
    }

    /**
     * Check if the specified type is a primitive or primitive type class or String.
     *
     * @param type - Field to check primitive/String for.
     * @return - Is primitive or String?
     */
    public static boolean isPrimitiveTypeOrString(@Nonnull Class<?> type) {
        if (isPrimitiveTypeOrClass(type)) {
            return true;
        }
        if (type == String.class) {
            return true;
        }
        return false;
    }

    /**
     * Get the parsed value of the type specified from the
     * string value passed.
     *
     * @param type  - Required value type
     * @param value - Input String value
     * @return - Parsed Value.
     */
    @SuppressWarnings("unchecked")
    public static Object parseStringValue(Class<?> type, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (isPrimitiveTypeOrString(type)) {
                return parsePrimitiveValue(type, value);
            } else if (type.isEnum()) {
                Class<Enum> et = (Class<Enum>) type;
                return Enum.valueOf(et, value);
            }
        }
        return null;
    }

    /**
     * Get the value of the primitive type parsed from the string value.
     *
     * @param type  - Primitive Type
     * @param value - String value
     * @return - Parsed Value
     */
    private static Object parsePrimitiveValue(Class<?> type, String value) {
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return Short.parseShort(value);
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return value.charAt(0);
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (type.equals(String.class)) {
            return value;
        }
        return null;
    }

    public static Object getNestedFieldValue(@Nonnull Object source,
                                             @Nonnull FieldPath path) throws Exception {
        Preconditions.checkArgument(path.getNodes() != null);
        Object value = source;
        Class<?> type = source.getClass();
        int index = 0;
        FieldPath.PathNode[] nodes = path.getNodes();
        while (index < nodes.length) {
            Field field = findField(type, nodes[index].name());
            if (field == null) {
                throw new Exception(String.format("Field not found. [type=%s][field=%s]",
                        type.getCanonicalName(), nodes[index].name()));
            }

            value = getFieldValue(value, field);
            if (nodes[index] instanceof FieldPath.CollectionPathNode) {
                FieldPath.CollectionPathNode node = (FieldPath.CollectionPathNode) nodes[index];
                int ii = Integer.parseInt(node.key());
                if (implementsInterface(List.class, field.getType())) {
                    List vl = (List) value;
                    value = vl.get(ii);
                } else if (implementsInterface(Set.class, field.getType())) {
                    Set vl = (Set) value;
                    Object[] arr = vl.toArray();
                    value = arr[ii];
                } else if (implementsInterface(Map.class, field.getType())) {
                    Map map = (Map) value;
                    Class<?> kt = getGenericMapKeyType(field);
                    Object kv = parseStringValue(kt, node.key());
                    if (kv == null) {
                        break;
                    }
                    value = map.get(kv);
                }
                type = value.getClass();
            } else {
                type = field.getType();
            }
            if (value == null) {
                break;
            }
            index++;
        }
        return value;
    }


    /**
     * Get the value of the specified field from the object passed.
     * This assumes standard bean Getters/Setters.
     *
     * @param o     - Object to get field value from.
     * @param field - Field value to extract.
     * @return - Field value.
     * @throws Exception
     */
    public static Object getFieldValue(@Nonnull Object o, @Nonnull Field field) throws Exception {
        return getFieldValue(o, field, false);
    }

    public static Object getFieldValue(@Nonnull Object o, @Nonnull Field field, boolean ignore)
            throws Exception {
        Preconditions.checkArgument(o != null);
        Preconditions.checkArgument(field != null);

        String method = "get" + StringUtils.capitalize(field.getName());

        Method m = MethodUtils.getAccessibleMethod(o.getClass(), method);
        if (m == null) {
            method = field.getName();
            m = MethodUtils.getAccessibleMethod(o.getClass(), method);
        }

        if (m == null) {
            Class<?> type = field.getType();
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                method = "is" + StringUtils.capitalize(field.getName());
                m = MethodUtils.getAccessibleMethod(o.getClass(), method);
            }
        }

        if (m == null)
            if (!ignore)
                throw new Exception("No accessable method found for field. [field="
                        + field.getName() + "][class="
                        + o.getClass().getCanonicalName() + "]");
            else return null;

        return MethodUtils.invokeMethod(o, method);
    }

}
