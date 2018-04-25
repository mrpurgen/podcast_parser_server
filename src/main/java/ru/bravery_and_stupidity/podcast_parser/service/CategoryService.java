package ru.bravery_and_stupidity.podcast_parser.service;

import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getList();
    void addList(List<Category> categories);
    Long getId(String name);
}
