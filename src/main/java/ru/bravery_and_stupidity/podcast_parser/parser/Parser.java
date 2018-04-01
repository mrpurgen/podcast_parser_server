package ru.bravery_and_stupidity.podcast_parser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.service.CategoryService;
import ru.bravery_and_stupidity.podcast_parser.service.PodcastService;

@Component
final public class Parser implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    Thread thread;

    List<Category> categories;
    List<Podcast> podcasts;

    long podcastId = 0;

    @Autowired
    CategoryService categoryService;

    @Autowired
    PodcastService podcastService;

    public Parser(){
        categories = new ArrayList<>(100);
        podcasts = new ArrayList<>(100);

        thread = new Thread(this, "thread_parser");
        thread.start();
    }

    private void CategoryParser() throws IOException {
        String START_URL = "http://pravradio.ru/audioarchive";

        Document pageAudioArchive = Jsoup.connect(START_URL).get();

        Elements classObject = pageAudioArchive.select("div.obj-categories");
        Elements links = classObject.select("a[href]");

        categories.add(new Category(0L, classObject.select("li.active").get(0).ownText(), START_URL));

        for (int i = 0; i < links.size(); i++) {
            categories.add(new Category((long)(i + 1), links.get(i).attr("title"), links.get(i).attr("abs:href")));
        }
    }

    private void PodcastParserAllCategories() throws IOException{
        for (Category category: categories) {
            PodcastParserSimpleCategories(category);
        }
    }

    private void PodcastParserSimpleCategories(Category category) throws IOException{
        Document page = Jsoup.connect(category.getUrl()).get();
        Elements audioItems = page.select("h2.entry-title");
        Elements audioLinks = page.select("div.mod-audio-links").select("a[href]");


        for (int i = 0; i < audioItems.size(); i++) {
            podcasts.add(new Podcast(podcastId, category.getId(), audioItems.get(i).ownText(), new Date(0L), audioLinks.get(i).attr("abs:href")));
            podcastId++;
        }

    }

    private void ParserInitional(){
        try{
            CategoryParser();
            PodcastParserAllCategories();
        }
        catch(IOException e){

        }
        categoryService.addList(categories);
        podcastService.addList(podcasts);
    }

    @Override
    public void run() {
        ParserInitional();
    }
}
