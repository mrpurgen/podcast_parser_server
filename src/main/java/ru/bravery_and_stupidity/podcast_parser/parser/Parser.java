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
import java.util.*;
import java.util.concurrent.TimeUnit;

import ru.bravery_and_stupidity.podcast_parser.model.Podcast;
import ru.bravery_and_stupidity.podcast_parser.service.CategoryService;
import ru.bravery_and_stupidity.podcast_parser.service.PodcastService;

@Component
final public class Parser implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    Thread thread;

    List<Category> categories;
    List<Podcast> podcasts;
    Document page;

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

        page = Jsoup.connect(START_URL).get();

        Elements classObject = page.select("div.obj-categories");
        Elements links = classObject.select("a[href]");

        categories.add(new Category(0L, classObject.select("li.active").get(0).ownText(), START_URL));

        for (int i = 0; i < links.size(); i++) {
            categories.add(new Category((long)(i + 1), links.get(i).attr("title"), links.get(i).attr("abs:href")));
        }
    }

    private void PodcastParserAllCategories() throws IOException, InterruptedException {
     //   for (Category category: categories) {
         //   logger.info("parsing " + category.getName());
            PodcastParserSimpleCategories(categories.get(0));
            TimeUnit.SECONDS.sleep(1);
       // }
    }

    private void PodcastParserSimpleCategories(Category category) throws IOException, InterruptedException {
        page = Jsoup.connect(category.getUrl()).get();

        //PodcastParserRecords(category);
      //  PodcastParserMonths(category);
        PodcastParserYears(category);
    }

    private void PodcastParserYears(Category category) throws InterruptedException, IOException {

        ListIterator<Element>yearIterator =  page.select("h4").select("a[href]").listIterator();

        while(yearIterator.hasNext()){
            Element year = yearIterator.next();
            logger.info("year " + year.attr("abs:href"));
            page = Jsoup.connect(year.attr("abs:href")).timeout(10*1000).get();

            Elements bufElements = new Elements(page.select("h4").select("a[href]"));

            if (bufElements.size() > 1) {
                yearIterator.remove();
                yearIterator.add(page.select("h4").select("a[href]").first());
                yearIterator.previous();
            }

            TimeUnit.SECONDS.sleep(1);

            PodcastParserRecords(category);
            PodcastParserMonths(category);
        }
    }

    private void PodcastParserMonths(Category category) throws InterruptedException, IOException {
        Elements months = page.select("dfn").select("a[href]");

        ListIterator<Element> monthIterator = months.listIterator(months.size());

        while (monthIterator.hasPrevious()){
            Element month = monthIterator.previous();

            logger.info("mount " + month.attr("abs:href"));
            page = Jsoup.connect(month.attr("abs:href")).timeout(10*1000).get();
            TimeUnit.SECONDS.sleep(1);

            PodcastParserRecords(category);
        }
    }

    private void PodcastParserRecords(Category category) throws InterruptedException, IOException{
        Elements audioItems = page.select("h2.entry-title");;
        Elements audioLinks = page.select("div.mod-audio-links").select("a[href]");;

        for(int i = 0; i < audioItems.size(); i++){
            logger.info("podcast " +  audioItems.get(i).ownText());
            podcasts.add(new Podcast(category.getId(), audioItems.get(i).ownText(), new Date(0L), audioLinks.get(i).attr("abs:href")));
        }
    }

    private void ParserInitional(){
        try{
            CategoryParser();
            try {
                PodcastParserAllCategories();
            }
            catch (InterruptedException e){
                logger.info("wait error " + e);
            }
        }
        catch(IOException e){
            logger.info("connect error" + e);
        }
        categoryService.addList(categories);
        podcastService.addList(podcasts);

        logger.info("parser succ");
    }

    @Override
    public void run() {
        ParserInitional();
    }
}
