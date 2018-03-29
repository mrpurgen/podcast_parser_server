package ru.bravery_and_stupidity.podcast_parser.repository;

import ru.bravery_and_stupidity.podcast_parser.repository.specification.SqlSpecification;

import java.util.List;

public interface Repository<T> {
    void add(T item);
    void add(Iterable<T> iterable);
    void update(T item);
    int remove(T item);
    int remove(SqlSpecification specification);
    List<T> query(SqlSpecification specification);
}
