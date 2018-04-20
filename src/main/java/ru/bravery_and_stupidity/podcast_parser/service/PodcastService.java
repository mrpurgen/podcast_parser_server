package ru.bravery_and_stupidity.podcast_parser.service;

import ru.bravery_and_stupidity.podcast_parser.model.Podcast;

import java.util.List;

public interface PodcastService {
    List<Podcast> getList(Long categoryId);
    List<Podcast> getListRangeID(Long categoryId, Long minId, Long maxId);
    void addList(List<Podcast> podcasts);
}
