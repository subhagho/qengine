package com.codekutter.qengine.model;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import lombok.NonNull;

public interface Condition {

    void validate() throws ValidationException;

    boolean evaluate(@NonNull Object data) throws EvaluationException;
}
