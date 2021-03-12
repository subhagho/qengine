package com.codekutter.qengine.model.values;

import com.codekutter.qengine.model.DataType;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class ConstantCollection<T> extends Value {
    private List<T> values;

    public ConstantCollection(@NonNull DataType.BasicDataType<T> dataType) {
        super(ValueType.Constant, dataType);
    }

    public ConstantCollection<T> add(@NonNull T value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);

        return this;
    }

    public ConstantCollection<T> addAll(@NonNull Collection<T> source) {
        if (!source.isEmpty()) {
            if (values == null) {
                values = new ArrayList<>(source.size());
            }
            values.addAll(source);
        }
        return this;
    }

    public boolean isEmpty() {
        return (values == null || values.isEmpty());
    }

    public boolean contains(@NonNull T value) {
        if (values != null) {
            return values.contains(value);
        }
        return false;
    }
}
