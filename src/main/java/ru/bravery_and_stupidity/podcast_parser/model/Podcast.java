package ru.bravery_and_stupidity.podcast_parser.model;

import org.jetbrains.annotations.NotNull;
import ru.bravery_and_stupidity.podcast_parser.dto.PodcastDto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final public class Podcast {

    @NotNull
    private Long id = 0L;

    @NotNull
    private Long categoryId = 0L;

    @NotNull
    private String name = "";

    @NotNull
    private Date date = new Date(0L);

    @NotNull
    private String url = "";

    public Podcast(@NotNull Long id, @NotNull Long categoryId, @NotNull String name, @NotNull Date date, @NotNull String url) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.date = date;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Podcast podcast = (Podcast) o;
        return Objects.equals(id, podcast.id) &&
                Objects.equals(categoryId, podcast.categoryId) &&
                Objects.equals(name, podcast.name) &&
                Objects.equals(date, podcast.date) &&
                Objects.equals(url, podcast.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, categoryId, name, date, url);
    }

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
    public PodcastDto mapToDto() {
        PodcastDto podcast = new PodcastDto();
        podcast.setId(id);
        podcast.getCategoryId();
        podcast.setName(name);
        podcast.setUrl(url);
        podcast.setDate(date);
        return podcast;
    }

    @NotNull
    public static List<PodcastDto> mapToDtos(@NotNull List<Podcast> podcasts) {
        return podcasts
                .stream()
                .map(Podcast::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
