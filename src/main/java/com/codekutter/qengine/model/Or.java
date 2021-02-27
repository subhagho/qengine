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
        if (left() instanceof Condition) {
            throw new ValidationException("Left condition missing or invalid.");
        }
        ((Condition)left()).validate();
        if (right() instanceof Condition) {
            throw new ValidationException("Right condition missing or invalid");
        }
        ((Condition)right()).validate();
    }

    @Override
    public boolean evaluate(@NonNull Object data) throws EvaluationException {
        try {
            validate();
            return (((BaseCondition)left()).evaluate(data) || ((BaseCondition)right()).evaluate(data));
        } catch (EvaluationException e) {
            throw e;
        } catch (Throwable t) {
            throw new EvaluationException(t);
        }
    }
}
