package com.cybozu.spring.data.jdbc.core;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.cybozu.spring.data.jdbc.core.util.BeanFactoryUtil;
import com.cybozu.spring.data.jdbc.core.util.EntityUtil;

class JdbcTemplateRepositoryInternal<T> implements JdbcTemplateRepository<T> {
    private final Class<T> domainClass;
    private final BeanFactory beanFactory;
    private final Configuration configuration;

    JdbcTemplateRepositoryInternal(BeanFactory beanFactory, Configuration configuration, Class<T> domainClass) {
        this.beanFactory = beanFactory;
        this.configuration = configuration;
        this.domainClass = domainClass;
    }

    private NamedParameterJdbcOperations operations() {
        return BeanFactoryUtil.getBeanByNameOrType(beanFactory, configuration.getOperationsBeanName(),
                NamedParameterJdbcOperations.class);
    }

    @Override
    public void insert(T entity) {
        JdbcTemplate jdbcTemplate = (JdbcTemplate) operations().getJdbcOperations();

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(EntityUtil.tableName(domainClass));
        Map<String, Object> values = EntityUtil.values(entity, domainClass, false);
        jdbcInsert.execute(values);
    }

    @Override
    public void update(T entity) {
        String tableName = EntityUtil.tableName(domainClass);
        String setClause = EntityUtil.columnNamesExceptKeys(domainClass).stream().map(c -> c + " = :" + c)
                .collect(Collectors.joining(" , "));
        String keyClause = EntityUtil.keyNames(domainClass).stream().map(k -> k + " = :" + k)
                .collect(Collectors.joining(" AND "));
        String query = "UPDATE " + tableName + " SET " + setClause + " WHERE " + keyClause;
        Map<String, Object> values = EntityUtil.values(entity, domainClass, true);
        operations().update(query, values);
    }
}
