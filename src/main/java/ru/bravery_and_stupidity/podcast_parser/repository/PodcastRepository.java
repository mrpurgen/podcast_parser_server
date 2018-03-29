package ru.bravery_and_stupidity.podcast_parser.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.bravery_and_stupidity.podcast_parser.model.Category;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.model.validator.ModelValidator;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SqlSpecification;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
final public class PodcastRepository implements Repository<Podcast> {

    private final JdbcTemplate jdbcTemplate;

    private static String NOT_NULL_MSG = "Parameter must not be null";
    private RowMapper<Podcast> rowMapper = (resultSet, rowNum) ->
        new Podcast(resultSet.getLong("id"),
            resultSet.getLong("categoryId"),
            resultSet.getString("name"),
            resultSet.getDate("date"),
            resultSet.getString("url"));

    public PodcastRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Podcast item) {
        ModelValidator.validate(item);
        String sql = "INSERT INTO podcast (categoryId ,name, date, url) VALUES(?,?,?,?)";
        jdbcTemplate.update(sql, item.getCategoryId(), item.getName(), item.getDate(), item.getUrl());
    }

    @Override
    public void add(Iterable<Podcast> iterable) {
        Assert.notNull(iterable, NOT_NULL_MSG);
        String sql = "INSERT INTO podcast (categoryId ,name, date, url) VALUES(?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, createBatchPreparedStatement(createList(iterable)));
    }

    private List<Podcast> createList(Iterable<Podcast> iterable) {
        List<Podcast> podcasts = new ArrayList<>();
        iterable.forEach(item -> {
            ModelValidator.validate(item);
            podcasts.add(item);
        });
        return podcasts;
    }

    private BatchPreparedStatementSetter createBatchPreparedStatement(List<Podcast> podcasts) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, podcasts.get(i).getCategoryId());
                ps.setString(2, podcasts.get(i).getName());
                ps.setDate(3, podcasts.get(i).getDate());
                ps.setString(4, podcasts.get(i).getUrl());
            }
            @Override
            public int getBatchSize() {
                return podcasts.size();
            }
        };
    }

    @Override
    public void update(Podcast item) {
        ModelValidator.validate(item);
        String sql = "UPDATE podcast SET categoryId = ?, name = ?, date = ?, url = ? WHERE id = ?";
        jdbcTemplate.update(sql,item.getCategoryId(), item.getName(),item.getDate(), item.getUrl(), item.getId());
    }

    @Override
    public int remove(Podcast item) {
        ModelValidator.validate(item);
        String sql = "DELETE FROM podcast WHERE id = ?";
        System.out.println(jdbcTemplate);
        return jdbcTemplate.update(sql, item.getId());
    }

    @Override
    public int remove(SqlSpecification specification) {
        Assert.notNull(specification, NOT_NULL_MSG);
        String sql = "DELETE FROM podcast WHERE " + specification.toSqlQuery();
        return jdbcTemplate.update(sql);
    }

    @Override
    public List<Podcast> query(SqlSpecification specification) {
        Assert.notNull(specification, NOT_NULL_MSG);
        String sql = "SELECT * FROM podcast WHERE " + specification.toSqlQuery();
        return jdbcTemplate.query(sql, rowMapper);
    }
}
