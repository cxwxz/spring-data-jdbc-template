package com.cybozu.spring.data.jdbc.core.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;

import org.springframework.beans.BeanUtils;

import com.cybozu.spring.data.jdbc.core.util.Accessor;
import com.cybozu.spring.data.jdbc.core.util.EntityUtil;

public abstract class AbstractEntityMapper<T> implements EntityMapper<T> {
    @Getter(AccessLevel.PROTECTED)
    private Class<T> mappedClass;

    @Getter(AccessLevel.PROTECTED)
    private List<Accessor> accessors;

    @Override
    public void initialize(Class<T> mappedClass) {
        EntityMapper.super.initialize(mappedClass);
        this.mappedClass = mappedClass;
        this.accessors = Collections.unmodifiableList(EntityUtil.getAccessors(mappedClass));
    }

    @Override
    public T createInstance() {
        return BeanUtils.instantiate(mappedClass);
    }

    @Override
    public Map<String, Class<?>> types() {
        Map<String, Class<?>> result = new HashMap<>();
        for (Accessor accessor : accessors) {
            String columnName = EntityUtil.columnName(accessor);
            result.put(columnName, accessor.getValueType());
        }
        return result;
    }
}
