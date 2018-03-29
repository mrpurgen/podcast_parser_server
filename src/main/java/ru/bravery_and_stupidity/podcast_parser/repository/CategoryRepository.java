package ru.bravery_and_stupidity.podcast_parser.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.bravery_and_stupidity.podcast_parser.model.Category;
import ru.bravery_and_stupidity.podcast_parser.model.validator.ModelValidator;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SqlSpecification;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
final public class CategoryRepository implements Repository<Category> {

    private final JdbcTemplate jdbcTemplate;
    private static String NOT_NULL_MSG = "Parameter must not be null";
    private RowMapper<Category> rowMapper = (resultSet, rowNum) ->
         new Category(resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("url"));

    CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(@NotNull Category item) {
        ModelValidator.validate(item);
        String sql = "INSERT INTO category (name, url) VALUES(?,?)";
        jdbcTemplate.update(sql, item.getName(), item.getUrl());
    }

    @Override
    public void add(@NotNull Iterable<Category> iterable) {
        Assert.notNull(iterable, NOT_NULL_MSG);
        String sql = "INSERT INTO category (name, url) VALUES(?,?)";
        jdbcTemplate.batchUpdate(sql, createBatchPreparedStatement(createList(iterable)));
    }

    private List<Category> createList(Iterable<Category> iterable) {
        List<Category> categories = new ArrayList<>();
        iterable.forEach(item -> {
            ModelValidator.validate(item);
            categories.add(item);
        });
        return categories;
    }

    private BatchPreparedStatementSetter createBatchPreparedStatement(List<Category> categories) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, categories.get(i).getName());
                ps.setString(2, categories.get(i).getUrl());
            }
            @Override
            public int getBatchSize() {
                return categories.size();
            }
        };
    }

    @Override
    public void update(@NotNull Category item) {
        ModelValidator.validate(item);
        String sql = "UPDATE category SET name = ?, url = ? WHERE id = ?";
        jdbcTemplate.update(sql,item.getName(), item.getUrl(), item.getId());
    }

    @Override
    public int remove(@NotNull Category item) {
        ModelValidator.validate(item);
        String sql = "DELETE FROM category WHERE id = ?";
        System.out.println(jdbcTemplate);
        return jdbcTemplate.update(sql, item.getId());
    }

    @Override
    public int remove(@NotNull SqlSpecification specification) {
        Assert.notNull(specification, NOT_NULL_MSG);
        String sql = "DELETE FROM category WHERE " + specification.toSqlQuery();
        return jdbcTemplate.update(sql);
    }

    @Override
    public List<Category> query(@NotNull SqlSpecification specification) {
        Assert.notNull(specification, NOT_NULL_MSG);
        String sql = "SELECT * FROM category WHERE " + specification.toSqlQuery();
        return jdbcTemplate.query(sql, rowMapper);
    }
}
