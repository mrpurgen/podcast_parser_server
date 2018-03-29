package ru.bravery_and_stupidity.podcast_parser.model;
import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final public class Category {
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

    public Category(@NotNull Long id, @NotNull String name, @NotNull String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(name, category.name) &&
                Objects.equals(url, category.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, url);
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
    public CategoryDto mapToDto() {
        CategoryDto category = new CategoryDto();
        category.setId(id);
        category.setName(name);
        category.setUrl(url);
        return category;
    }

    @NotNull
    public static List<CategoryDto> mapToDtos(@NotNull List<Category> categories) {
        return categories
                .stream()
                .map(Category::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
