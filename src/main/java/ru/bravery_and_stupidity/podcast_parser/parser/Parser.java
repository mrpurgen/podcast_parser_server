package ru.bravery_and_stupidity.podcast_parser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bravery_and_stupidity.podcast_parser.Application;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.bravery_and_stupidity.podcast_parser.service.CategoryService;
import ru.bravery_and_stupidity.podcast_parser.service.CategoryServiceImp;
@Component
public class Parser implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    Thread thread;

    @Autowired
    CategoryService service;

    public Parser(){
        thread = new Thread(this, "thread_parser");
        thread.start();
    }

    public List<Category> CategoryParser() throws IOException {
        ArrayList<Category> categories = new ArrayList<>();

        Document pageAudioArchive = Jsoup.connect("http://pravradio.ru/audioarchive").get();

        Elements classObject = pageAudioArchive.select("div.obj-categories");
        Elements links = classObject.select("a[href]");

        for (int i = 0; i < links.size(); i++) {
            categories.add(new Category((long)i, links.get(i).attr("title"), links.get(i).attr("abs:href")));
        }
        return categories;
    }


    @Override
    public void run() {
        List<Category> categories = new ArrayList<Category>();

        try{
            categories = CategoryParser();
        }
        catch(IOException e){

        }
        service.addList(categories);
    }
}
