package com.codekutter.qengine.model.conditions;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.DataType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Equals<T> extends BaseCondition {
    private final DataType.BasicDataType<T> dataType;

    public Equals(@NonNull DataType.BasicDataType<T> dataType) {
        this.dataType = dataType;
    }

    @Override
    public void validate() throws ValidationException {
        if (left() == null) {
            throw new ValidationException("Left Vertex not set.");
        }
        if (right() == null) {
            throw new ValidationException("Right Vertex not set.");
        }
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        return false;
    }
}
