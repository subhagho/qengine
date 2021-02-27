package com.codekutter.qengine.model;

import com.codekutter.qengine.common.ConnectionManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class QueryOutput extends Value {
    private String query;
    private String connection;
    private ConnectionManager.ConnectionTypes connectionType = ConnectionManager.ConnectionTypes.Hibernate;

    public QueryOutput() {
        super(ValueType.Query);
    }
}
