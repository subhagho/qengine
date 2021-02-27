package com.codekutter.qengine.model;

import com.codekutter.qengine.common.ReferenceDataManager;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Collection;

@Getter
@Accessors(fluent = true)
public class ReferenceList extends Value{
    private final String name;

    public ReferenceList(@NonNull String name) {
        super(ValueType.Reference);
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getReferenceList(@NonNull Class<T> type) throws ClassCastException{
        Preconditions.checkArgument(dataType() != null);
        DataType dt = DataType.convert(type);
        if (dt == null) {
            throw new ClassCastException(String.format("Expected=%s, Passed=%s", dataType().name(), type.getCanonicalName()));
        }
        if (!dataType().equals(dt)) {
            throw new ClassCastException(String.format("Expected=%s, Passed=%s", dataType().name(), dt.name()));
        }
        return (Collection<T>) ReferenceDataManager.get();
    }
}
