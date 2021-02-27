package com.codekutter.qengine.model;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Group extends Vertex implements Condition {
    private Condition condition;

    @Override
    public void validate() throws ValidationException {
        if (condition == null) {
            throw new ValidationException("Empty Group condition.");
        }
        condition.validate();
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        try {
            validate();
        } catch (ValidationException ve) {
            throw new EvaluationException(ve);
        }
        return condition.evaluate(data);
    }
}
