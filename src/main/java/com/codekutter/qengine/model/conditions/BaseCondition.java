package com.codekutter.qengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public abstract class BaseCondition extends BooleanVertex {
    private Vertex left;
    private Vertex right;
}
