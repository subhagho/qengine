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

import com.codekutter.qengine.common.QueryCacheManager;
import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.values.FieldPath;
import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
public class ClassIndex<T> {
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
                Class<?> tt = f.getType();
                if (!Reflector.isPrimitiveTypeOrString(f.getType())
                        && f.getType() != Class.class && f.getType() != Object.class) {
                    if (Reflector.implementsInterface(List.class, f.getType())) {
                        tt = Reflector.getGenericListType(f);
                    } else if (Reflector.implementsInterface(Set.class, f.getType())) {
                        tt = Reflector.getGenericSetType(f);
                    } else if (Reflector.implementsInterface(Map.class, f.getType())) {
                        tt = Reflector.getGenericMapValueType(f);
                    }
                    if (!tt.equals(type)) {
                        ClassIndex<?> ci = QueryCacheManager.get().getClassIndex(tt);
                        if (ci == null) {
                            throw new Exception(String.format("Error getting class index for type. [type=%s]", tt.getCanonicalName()));
                        }
                        Map<String, IndexedField> idx = ci.index();
                        for (String key : idx.keySet()) {
                            String k = String.format("%s/%s", p, key);
                            IndexedField ff = idx.get(key);
                            index.put(k, ff);
                        }
                    } else {
                        setup(tt, path);
                    }
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

    public Object getFieldValue(@NonNull Object source, @NonNull FieldPath path) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParseException {
        Preconditions.checkArgument(index.size() > 0);
        Preconditions.checkArgument(Reflector.isSuperType(type, source.getClass()));
        return getFieldValue(source, path, 0, null);
    }

    private Object getFieldValue(Object source, FieldPath path, int idx, String journey) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParseException {
        String p = journey;
        FieldPath.PathNode pn = path.getNodes()[idx];
        if (Strings.isNullOrEmpty(p)) {
            p = pn.name();
        } else {
            p = String.format("%s/%s", p, pn.name());
        }
        IndexedField ff = index.get(p);
        if (ff == null) {
            throw new IllegalArgumentException(String.format("Specified field path not found. [type=%s][path=%s]", type.getCanonicalName(), p));
        }
        Object value = MethodUtils.invokeMethod(source, ff.getter.getName());
        if (value != null) {
            if (idx == (path.getNodes().length - 1)) {
                return value;
            }
            if (Reflector.implementsInterface(List.class, value.getClass())) {
                if (!Reflector.implementsInterface(List.class, ff.field.getType())) {
                    throw new IllegalArgumentException(String.format("Type mismatch. [object=%s][field type=%s]",
                            value.getClass().getCanonicalName(), ff.field.getType().getCanonicalName()));
                }
                if (!(pn instanceof FieldPath.CollectionPathNode)) {
                    throw new IllegalArgumentException(
                            String.format("Invalid Path node. [expected collection path node.][path=%s][node=%s]",
                                    path.getPath(), pn.name()));
                }
                String sii = ((FieldPath.CollectionPathNode) pn).key();
                Preconditions.checkState(!Strings.isNullOrEmpty(sii));
                int ii = Integer.parseInt(sii);
                List<?> lv = (List<?>) value;
                value = lv.get(ii);
            }
            if (Reflector.implementsInterface(Map.class, value.getClass())) {
                if (!Reflector.implementsInterface(Map.class, ff.field.getType())) {
                    throw new IllegalArgumentException(String.format("Type mismatch. [object=%s][field type=%s]",
                            value.getClass().getCanonicalName(), ff.field.getType().getCanonicalName()));
                }
                if (!(pn instanceof FieldPath.CollectionPathNode)) {
                    throw new IllegalArgumentException(
                            String.format("Invalid Path node. [expected collection path node.][path=%s][node=%s]",
                                    path.getPath(), pn.name()));
                }
                String sii = ((FieldPath.CollectionPathNode) pn).key();
                Preconditions.checkState(!Strings.isNullOrEmpty(sii));
                Map<?, ?> map = (Map<?, ?>) value;
                Class<?> kt = Reflector.getGenericMapKeyType(ff.field);
                Object key = Reflector.parseValue(kt, sii);
                value = map.get(key);
            }
            return getFieldValue(value, path, idx + 1, p);
        }
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
}
