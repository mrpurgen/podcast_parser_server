package ru.bravery_and_stupidity.podcast_parser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.repository.PodcastRepository;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationByCategory;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationByIdRange;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SqlSpecification;

import java.util.List;

@Service
public class PodcastServiceImp implements PodcastService {

    @Autowired
    PodcastRepository repository;

    @Transactional
    @Override
    public List<Podcast> getList(Long categoryId) {
        return repository.query(new SpecificationByCategory(categoryId));
    }

    @Transactional
    @Override
    public List<Podcast> getListRangeID(Long categoryId, Long minId, Long maxId) {
        return repository.query(new SpecificationByIdRange(categoryId, minId, maxId));
    }

    @Transactional
    @Override
    public void addList(List<Podcast> podcasts) { repository.add(podcasts);}
}
