package com.codekutter.qengine.model.conditions;

import com.codekutter.qengine.model.BooleanVertex;
import com.codekutter.qengine.model.Vertex;
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
