package ru.bravery_and_stupidity.podcast_parser.repository;

import org.junit.*;
import org.junit.Assert;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.bravery_and_stupidity.podcast_parser.model.Category;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationById;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationByIdRange;
import ru.bravery_and_stupidity.podcast_parser.repository.specification.SpecificationAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CategoryRepositoryTest {
    private EmbeddedDatabase database;
    private CategoryRepository repository;
    private ArrayList<Category> etalonCategories = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        database = new EmbeddedDatabaseBuilder()
            .addDefaultScripts()
            .setType(EmbeddedDatabaseType.HSQL)
            .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(database);
        repository = new CategoryRepository(jdbcTemplate);
        createEtalonData();
    }

    private void createEtalonData() {
        etalonCategories.add(new Category(1L,"Heavy metal","http://heavy-metal"));
        etalonCategories.add(new Category(2L,"Punk rock","http://punk-rock"));
        etalonCategories.add(new Category(3L,"Alternative","http://alternative"));
        etalonCategories.add(new Category(4L,"New metal","http://new-metal"));
    }

    @After
    public void tearDown() throws Exception {
        database.shutdown();
    }

    @Test
    public void add() throws Exception {
        Category category = new Category(5L,"Glam Rock","http://glam-rock");
        repository.add(category);
        List<Category> categories = repository.query(new SpecificationById(5L));
        Assert.assertEquals(category,categories.get(0));
    }

    @Test
    public void addList() throws Exception {
        List<Category> categories = Arrays.asList(new Category(5L,"Glam Rock","http://glam-rock"),
            new Category(6L,"Glam Rock1","http://glam-rock1"),
            new Category(7L,"Glam Rock2","http://glam-rock2"));
        repository.add(categories);
        etalonCategories.addAll(categories);
        Assert.assertArrayEquals(etalonCategories.toArray(), repository.query(new SpecificationAll()).toArray());
    }

    @Test
    public void update() throws Exception {
        Category category = etalonCategories.get(0);
        category.setName("Post rock");
        category.setUrl("http://post-rock");
        repository.update(category);
        Assert.assertArrayEquals(etalonCategories.toArray(), repository.query(new SpecificationAll()).toArray());
    }

    @Test(expected = IllegalStateException.class)
    public void updateWithNegativeId() throws Exception {
        repository.update(new Category(-1L,"Post rock","http://post-rock"));
    }

    @Test
    public void remove() throws Exception {
        Assert.assertEquals(1,repository.remove(etalonCategories.get(2)));
        Assert.assertEquals(0,repository.remove(etalonCategories.get(2)));
    }

    @Test
    public void removeByIdRange() throws Exception {
//        Assert.assertEquals(1,repository.remove(new SpecificationByIdRange(3,3)));
//        Assert.assertEquals(0,repository.remove(new SpecificationByIdRange(3,3)));
    }

    @Test
    public void getAll() throws Exception {
        List<Category> resultCategories = repository.query(new SpecificationAll());
        Assert.assertArrayEquals(etalonCategories.toArray(),resultCategories.toArray());
    }
}