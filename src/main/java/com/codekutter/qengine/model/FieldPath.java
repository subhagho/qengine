package com.codekutter.qengine.model;

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


    private static final String PARAM_REGEX = "(\\w+)\\[\\s*(\\w+)\\s*\\]";
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
}
