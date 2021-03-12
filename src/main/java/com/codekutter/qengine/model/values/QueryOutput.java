package com.codekutter.qengine.model.values;

import com.codekutter.qengine.common.ConnectionManager;
import com.codekutter.qengine.model.DataType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class QueryOutput extends Value {
    private String query;
    private String connection;
    private ConnectionManager.ConnectionTypes connectionType = ConnectionManager.ConnectionTypes.Hibernate;

    public QueryOutput(@NonNull DataType dataType) {
        super(ValueType.Query, dataType);
    }
}
