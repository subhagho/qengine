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
public class Or extends BaseCondition {

    @Override
    public void validate() throws ValidationException {
        if (left() instanceof BooleanVertex) {
            throw new ValidationException("Left condition missing or invalid.");
        }
        ((BooleanVertex)left()).validate();
        if (right() instanceof BooleanVertex) {
            throw new ValidationException("Right condition missing or invalid");
        }
        ((BooleanVertex)right()).validate();
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        try {
            validate();
            return (((BooleanVertex)left()).evaluate(data) || ((BooleanVertex)right()).evaluate(data));
        } catch (EvaluationException e) {
            throw e;
        } catch (Throwable t) {
            throw new EvaluationException(t);
        }
    }
}
