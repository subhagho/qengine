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

package com.codekutter.qengine.model.values;

import com.codekutter.qengine.model.ClassIndex;
import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class FieldPath {
    private static final String PARAM_REGEX = "(\\w+)\\[\\s*(\\S+)\\s*\\]";
    private static final Pattern PARAM_PATTERN = Pattern.compile(PARAM_REGEX);
    private String path;
    private PathNode[] nodes;

    public FieldPath withPath(@NonNull String path, @NonNull Class<?> type) throws IllegalArgumentException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path));
        String[] parts = path.split("/");
        nodes = new PathNode[parts.length];
        String journey = null;
        for (int ii = 0; ii < parts.length; ii++) {
            PathNode pn = parse(parts[ii]);
            pn.sequence = ii;
            if (ii == 0) {
                journey = pn.name;
            } else {
                journey = String.format("%s.%s", journey, pn.name);
            }
            Field fd = Reflector.findField(type, journey);
            if (fd == null) {
                throw new IllegalArgumentException(String.format("Error resolving field. [type=%s][path=%s]", type.getCanonicalName(), journey));
            }
            pn.field = fd;
            nodes[ii] = pn;
        }
        this.path = path;
        return this;
    }

    public FieldPath withPath(@NonNull String path, @NonNull ClassIndex<?> index) throws IllegalArgumentException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path));
        String[] parts = path.split("/");
        nodes = new PathNode[parts.length];
        String journey = null;
        for (int ii = 0; ii < parts.length; ii++) {
            PathNode pn = parse(parts[ii]);
            pn.sequence = ii;
            if (ii == 0) {
                journey = pn.name;
            } else {
                journey = String.format("%s/%s", journey, pn.name);
            }
            ClassIndex.IndexedField fd = index.find(journey);
            if (fd == null) {
                throw new IllegalArgumentException(String.format("Error resolving field. [type=%s][path=%s]", index.type().getCanonicalName(), journey));
            }
            pn.field = fd.field();
            nodes[ii] = pn;
        }
        this.path = path;
        return this;
    }

    private PathNode parse(String name) throws IllegalArgumentException {
        name = name.trim();
        Matcher matcher = PARAM_PATTERN.matcher(name);
        if (matcher.matches()) {
            String n = matcher.group(1);
            if (Strings.isNullOrEmpty(n)) {
                throw new IllegalArgumentException(String.format("Error extracting field name. [value=%s]", name));
            }
            String k = matcher.group(2);
            if (Strings.isNullOrEmpty(k)) {
                throw new IllegalArgumentException(String.format("Error extracting field key. [value=%s]", name));
            }
            CollectionPathNode pn = new CollectionPathNode();
            pn.name(n);
            pn.key = k;

            return pn;
        }
        PathNode pn = new PathNode();
        pn.name = name;

        return pn;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class PathNode {
        private String name;
        private int sequence;
        private Field field;
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class CollectionPathNode extends PathNode {
        private String key;
    }
}
