package com.codekutter.qengine.model;

import com.codekutter.qengine.common.EvaluationException;
import com.codekutter.qengine.common.ValidationException;
import lombok.NonNull;

public abstract class BooleanVertex extends Vertex {

    public abstract void validate() throws ValidationException;

    public abstract boolean evaluate(@NonNull Object data) throws EvaluationException;
}
