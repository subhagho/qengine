package com.codekutter.qengine.model;

import com.codekutter.qengine.common.ValidationException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class Condition extends Vertex {
    private Vertex left;
    private Vertex right;

    public abstract void validate() throws ValidationException;
}
