package com.codekutter.qengine.model;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.conditions.BaseCondition;
import com.codekutter.qengine.utils.Reflector;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(fluent = true)
public class Query<T> {
    private final Class<T> type;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<String, String> parameters;
    private BaseCondition condition;

    public Query(@NonNull Class<T> type) {
        this.type = type;
    }

    public Query<T> addParameter(@NonNull String key, String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        return this;
    }

    public String getParameter(@NonNull String key) {
        if (parameters != null) {
            return parameters.get(key);
        }
        return null;
    }

    public void validate() throws ValidationException {
        if (condition == null) {
            throw new ValidationException("Query condition not set.");
        }
        condition.validate();
    }

    public boolean evaluate(@NonNull Object value) throws EvaluationException {
        try {
            validate();
            if (!Reflector.isSuperType(type, value.getClass())) {
                throw new EvaluationException(String.format("Type Mismatch: [expected=%s][type=%s]", type.getCanonicalName(), value.getClass().getCanonicalName()));
            }
            return condition.evaluate(value);
        } catch (Exception ex) {
            throw new EvaluationException(ex);
        }
    }
}
