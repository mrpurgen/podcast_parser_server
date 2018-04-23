package ru.bravery_and_stupidity.podcast_parser.repository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PodcastRepositoryTest {
    private EmbeddedDatabase database;
    private PodcastRepository repository;
    private ArrayList<Podcast> etalonPodcasts = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        database = new EmbeddedDatabaseBuilder()
                .addScripts("testSchema.sql","testData.sql")
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
        repository = new PodcastRepository(jdbcTemplate);
        createEtalonData();
    }

    private void createEtalonData() {
        etalonPodcasts.add(new Podcast(1L, 1L,"Heavy metal radio", Date.valueOf("2015-02-11"),"http://heavy-metal-radio"));
        etalonPodcasts.add(new Podcast(2L, 1L,"Heavy metal radio2", Date.valueOf("2015-02-15"),"http://heavy-metal-radio2"));
        etalonPodcasts.add(new Podcast(3L, 1L,"Heavy metal radio3", Date.valueOf("2015-02-1"),"http://heavy-metal-radio3"));
        etalonPodcasts.add(new Podcast(4L, 1L,"Heavy metal radio4", Date.valueOf("2016-01-14"),"http://heavy-metal-radio4"));
        etalonPodcasts.add(new Podcast(5L, 2L,"Punk rock radio", Date.valueOf("2011-05-12"),"http://punk-rock-radio"));
        etalonPodcasts.add(new Podcast(6L, 2L,"Punk rock radio2", Date.valueOf("2012-03-23"),"http://punk-rock-radio2"));
        etalonPodcasts.add(new Podcast(7L, 4L,"New metal radio", Date.valueOf("2008-05-21"),"http://new-metal-radio"));
    }

    @After
    public void tearDown() throws Exception {
        database.shutdown();
    }

    @Test
    public void add() throws Exception {
        Podcast podcast = new Podcast(8L,1L,"Glam rock radio",Date.valueOf("2011-05-12"),"http://glam-rock-radio");
        repository.add(podcast);
        List<Podcast> podcasts = repository.query(new SpecificationById(8L));
        Assert.assertEquals(podcast,podcasts.get(0));
    }

    @Test
    public void addList() throws Exception {
        List<Podcast> podcasts = Arrays.asList(new Podcast(8L,1L,"Glam Rock radio",Date.valueOf("2011-05-12"),"http://glam-rock-radio"),
            new Podcast(9L,1L,"Glam Rock radio2",Date.valueOf("2011-05-12"),"http://glam-rock-radio2"),
            new Podcast(10L,1L,"Glam Rock radio3",Date.valueOf("2011-05-12"),"http://glam-rock-radio3"));
        repository.add(podcasts);
        etalonPodcasts.addAll(podcasts);
        Assert.assertArrayEquals(etalonPodcasts.toArray(), repository.query(new SpecificationAll()).toArray());
    }

    @Test
    public void update() throws Exception {
        Podcast podcast = etalonPodcasts.get(0);
        podcast.setName("Post rock radio");
        podcast.setUrl("http://post-rock-radio");
        repository.update(podcast);
        Assert.assertArrayEquals(etalonPodcasts.toArray(), repository.query(new SpecificationAll()).toArray());
    }

    @Test
    public void remove() throws Exception {
        Assert.assertEquals(1,repository.remove(etalonPodcasts.get(2)));
        Assert.assertEquals(0,repository.remove(etalonPodcasts.get(2)));
    }

    @Test
    public void removeByIdRange() throws Exception {
//        Assert.assertEquals(3,repository.remove(new SpecificationByIdRange(1,3)));
//        Assert.assertEquals(0,repository.remove(new SpecificationByIdRange(1,3)));
    }

    @Test
    public void getAll() throws Exception {
        List<Podcast> resultCategories = repository.query(new SpecificationAll());
        Assert.assertArrayEquals(etalonPodcasts.toArray(),resultCategories.toArray());
    }

    @Test
    public void getPodcastsByCategory() throws Exception {
        etalonPodcasts.removeIf(item->item.getId() > 4L);
        Assert.assertArrayEquals(etalonPodcasts.toArray(),repository.query(new SpecificationByCategory(1L)).toArray());
    }

    @Test
    public void getPodcastsByDateRange() throws Exception {
        etalonPodcasts.removeIf(item->item.getId() > 4L);
        Assert.assertArrayEquals(etalonPodcasts.toArray(),repository.query(new SpecificationByDateRange(Date.valueOf("2015-01-01"), Date.valueOf("2017-01-01"))).toArray());
    }
}