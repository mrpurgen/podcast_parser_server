package ru.bravery_and_stupidity.podcast_parser.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bravery_and_stupidity.podcast_parser.model.Category;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.service.CategoryService;
import ru.bravery_and_stupidity.podcast_parser.service.PodcastService;

@Component
final public class Parser implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    Thread thread;

    WrapObject page;

    @Autowired
    CategoryService categoryService;

    @Autowired
    PodcastService podcastService;

    private ParserEngine parserEngine;

    public Parser(ParserEngine parserEngine){
        this.parserEngine = parserEngine;

        thread = new Thread(this, "thread_parser");
        thread.start();
    }

    private List<Category> categoryParser() throws IOException {
        String START_URL = "http://pravradio.ru/audioarchive";
        List<Category> categories = new ArrayList<>(100);

        page = parserEngine.getPage(START_URL);

        List classObject = parserEngine.getContentFromPage(page.getObject(), "div", "obj-categories");
        List liClass = parserEngine.getContentFromElement(classObject, "li", "active");

        categories.add(new Category(0L, parserEngine.getOwnText(liClass.get(0)), START_URL));

        List links = parserEngine.getLinks(classObject);
        links.forEach((x)->{
            categories.add(new Category(0L, parserEngine.getAttr(x, "title"), parserEngine.getAttr(x, "abs:href")));
        });

        return categories;
    }

    private void podcastParserAllCategories(List<Category> categories){
        categories.forEach((category -> {
            try {
                podcastParserSimpleCategories(category);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    private void podcastParserSimpleCategories(Category category) throws IOException, InterruptedException {
        ArrayList<String> yearsList = podcastParserYears(category);

        ListIterator<String> yearIterator = yearsList.listIterator(yearsList.size());
        while(yearIterator.hasPrevious()){
            try {
                ArrayList<String> monthsList = podcastParserMonths(yearIterator.previous());
                monthsList.forEach((element)->{
                    try {
                        podcastService.addList(podcastParserRecords(category, element));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (ParsingSelectException e){
                yearIterator.next();
                podcastService.addList(podcastParserRecords(category, yearIterator.previous()));
            }
        }
    }

    private ArrayList<String> podcastParserYears(Category category) throws InterruptedException, IOException {
        ArrayList<String> yearListString = new ArrayList<>(20);

        page = parserEngine.getPage(category.getUrl());

        /// Получение ссылки на последний год производится отдельно, так как ссылка на него отсутсвует
        /// в первоначальном списке
        List hForClass = parserEngine.getContentFromPage(page.getObject(), "h4");
        List links = parserEngine.getLinks(hForClass);
        String lastYear = parserEngine.getAttr(links.get(links.size() - 1), "abs:href");

        page = parserEngine.getPage(lastYear);

        hForClass = parserEngine.getContentFromPage(page.getObject(), "h4");
        links = parserEngine.getLinks(hForClass);

        yearListString.add(parserEngine.getAttr(links.get(links.size() - 1), "abs:href"));
        yearListString.add(lastYear);

        ListIterator<String>yearIterator = yearListString.listIterator(1);

        while(yearIterator.hasNext()){
            String year = yearIterator.next();

            page = parserEngine.getPage(year);

            hForClass = parserEngine.getContentFromPage(page.getObject(), "h4");
            links = parserEngine.getLinks(hForClass);

            if(links.size() <= 1){ break; }

            yearIterator.add(parserEngine.getAttr(links.get(0), "abs:href"));

            yearIterator.previous();

            TimeUnit.SECONDS.sleep(1);
        }
            return yearListString;
    }

    private ArrayList<String> podcastParserMonths(String year) throws IOException, ParsingSelectException {
        ArrayList<String> monthsList = new ArrayList<>(12);

        page = parserEngine.getPage(year);

        List dfnTags = parserEngine.getContentFromPage(page.getObject(), "dfn");
        List links = parserEngine.getLinks(dfnTags);

        links.forEach((x)->{
            monthsList.add(parserEngine.getAttr(x, "abs:href"));
        });

        if (monthsList.isEmpty()){
            throw new ParsingSelectException("список месяцув пуст");
        }

        page = parserEngine.getPage(monthsList.get(0));

        dfnTags = parserEngine.getContentFromPage(page.getObject(), "dfn");
        links = parserEngine.getLinks(dfnTags);

        monthsList.add(parserEngine.getAttr(links.get(links.size() - 1), "abs:href"));

        return monthsList;
    }

    private List<Podcast> podcastParserRecords(Category category, String url) throws IOException{
        List<Podcast> podcasts = new ArrayList<>(300);

        page = parserEngine.getPage(url);
        List modeAudioItems = parserEngine.getContentFromPage(page.getObject(), "div", "mod-audio-item");

        Long categoryId = categoryService.getId(category.getName());

        ListIterator modeAudioItemIterator = modeAudioItems.listIterator(modeAudioItems.size());
        while(modeAudioItemIterator.hasPrevious()){
            WrapObject modeAudioItem = new WrapObject(modeAudioItemIterator.previous());

            String title = podcastParserTitle(modeAudioItem);
            String link = podcastParserURL(modeAudioItem);
            java.sql.Date date = podcastParserRecordDate(modeAudioItem);

            logger.info("title: " + title);
            logger.info("date: " + date);
            logger.info("url: " + url);

            podcasts.add(new Podcast(0L, categoryId, title, date, link));
        }
        return podcasts;
    }

    private String podcastParserTitle(WrapObject mode_audio_item){
        List titles = parserEngine.getContentFromElement(mode_audio_item.getObject(), "h2.entry-title");
        return parserEngine.getOwnText(titles.get(0));
    }

    private String podcastParserURL(WrapObject mode_audio_item){
        List linksClass = parserEngine.getContentFromElement(mode_audio_item.getObject(), "div", "mod-audio-links");
        List links = parserEngine.getLinks(linksClass);

        return parserEngine.getAttr(links.get(0), "abs:href");
    }

    private java.sql.Date podcastParserRecordDate(WrapObject mod_audio_item) {
        java.sql.Date dateSql = new java.sql.Date(0L);

        DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", new RussianDateFormatSymbols());
        try {
            List childrens = parserEngine.getChildren(mod_audio_item.getObject());
            String date = parserEngine.getText(childrens.get(0));

            String newDate = new NormalizeDate().getNormalizeDate(date);
            java.util.Date dateUtil = dateFormat.parse(newDate);
            dateSql.setTime(dateUtil.getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return dateSql;
    }

    private void podcastParserLastRecords(List<Category> categories){
        categories.forEach(category -> {
            try {
                podcastParserRecords(category, category.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void parserInitional(){
        List<Category> categories;
        try{
            categories = categoryParser();
            categoryService.addList(categories);

            podcastParserAllCategories(categories);
        }
        catch(IOException e){
            logger.info("connect error" + e);
        }
        logger.info("parser succ");
    }

    private void parserLastRecords() {
        List<Category> categories;
        while (true) {
            try {
                categories = categoryParser();
                podcastParserLastRecords(categories);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        parserInitional();
        parserLastRecords();
    }
}
