package ru.bravery_and_stupidity.podcast_parser.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonNaming
public final class CategoryDto {
    @NotNull
    private Long id = 0L;

    @NotNull
    private String name = "";

    @NotNull
    private String url = "";

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    @NotNull
    public Category mapToModel() {
        Category category = new Category(id, name, url);
        return category;
    }

    @NotNull
    public static List<Category> mapToModels(@NotNull List<CategoryDto> categories) {
        return categories
                .stream()
                .map(CategoryDto::mapToModel)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
