package ru.bravery_and_stupidity.podcast_parser.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.bravery_and_stupidity.podcast_parser.dto.PodcastDto;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.service.PodcastService;

import java.util.List;

@RestController
@RequestMapping("/podcasts")
class PodcastController {

    @Autowired
    PodcastService service;

    @RequestMapping(value = "/getList/{categoryId}", method = RequestMethod.GET)
    @ResponseBody
    public List<PodcastDto> getListPodCasts(@PathVariable("categoryId") Long categoryId) {
        return Podcast.mapToDtos(service.getList(categoryId));
    }

    @RequestMapping(value = "/getListRangeID/{categoryId}/{minId}&{maxId}")
    public List<PodcastDto> getListPodcastsRangeID(@PathVariable("categoryId") Long categoryId,
                                                   @PathVariable("minId") Long minId,
                                                   @PathVariable("maxId") Long maxId){
        return Podcast.mapToDtos(service.getListRangeID(categoryId, minId, maxId));
    }
}
