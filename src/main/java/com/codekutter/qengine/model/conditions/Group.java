package com.codekutter.qengine.model.conditions;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import com.codekutter.qengine.model.BooleanVertex;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Group extends BooleanVertex {
    private BooleanVertex condition;

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
