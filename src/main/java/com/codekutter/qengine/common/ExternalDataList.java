package com.codekutter.qengine.common;

import com.codekutter.qengine.model.DataType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;

@Getter
@Setter
@Accessors(fluent = true)
public class ExternalDataList {
    private final String name;
    private final DataType.BasicDataType dataType;
    private String connection;
    private ConnectionManager.ConnectionTypes connectionType;
    private String query;
    private Collection<Object> params;
    private boolean cached = true;
    private long cacheTimeout = 10000 * 60; // Default Cache time out is 10 mins.
    private long cacheSize = 1000;

    public ExternalDataList(@NonNull String name, @NonNull DataType.BasicDataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }
}
