package ru.bravery_and_stupidity.podcast_parser.service;

import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> getList();
    void addList(List<Category> categories);
    Long getId(String name);
}
