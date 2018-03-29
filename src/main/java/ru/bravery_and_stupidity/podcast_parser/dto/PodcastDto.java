package ru.bravery_and_stupidity.podcast_parser.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonNaming
public final class PodcastDto {

    @NotNull
    private Long id = 0L;

    @NotNull
    private Long categoryId = 0L;

    @NotNull
    private String name = "";

    @NotNull
    private Date date;

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
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NotNull Long categoryId) {
        this.categoryId = categoryId;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NotNull Date date) {
        this.date = date;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    @NotNull
    public Podcast mapToModel() {
        return new Podcast(id,categoryId,name,date,url);
    }

    @NotNull
    public static List<Podcast> mapToModels(@NotNull List<PodcastDto> podcasts) {
        return podcasts
                .stream()
                .map(PodcastDto::mapToModel)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
