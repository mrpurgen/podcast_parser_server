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
import java.sql.Array;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
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

    private DateFormatSymbols russianMonth = new DateFormatSymbols(){
        @Override
        public String[] getMonths() {
            return new String[]{
                    "января",
                    "февраля",
                    "марта",
                    "апреля",
                    "мая",
                    "июня",
                    "июля",
                    "августа",
                    "сентября",
                    "октября",
                    "ноября",
                    "декабря"
            };
        }
    };

    private void CategoryParser() throws IOException {
        String START_URL = "http://pravradio.ru/audioarchive";

        page = Jsoup.connect(START_URL).get();

        Elements classObject = page.select("div.obj-categories");

        categories.add(new Category(0L, classObject.select("li.active").first().ownText(), START_URL));

        classObject.select("a[href]").forEach((consumer)->{
            categories.add(new Category((long)consumer.elementSiblingIndex(), consumer.attr("title"), consumer.attr("abs:href")));
        });
    }

    private void PodcastParserAllCategories() throws IOException, InterruptedException {
     //   for (Category category: categories) {
         //   logger.info("parsing " + category.getName());
            PodcastParserSimpleCategories(categories.get(0));
            TimeUnit.SECONDS.sleep(1);
       // }
    }

    private void PodcastParserSimpleCategories(Category category) throws IOException, InterruptedException {
        ArrayList<String> yearsList = PodcastParserYears(category);

        ListIterator<String> yearIterator = yearsList.listIterator(yearsList.size());
        while(yearIterator.hasPrevious()){
            ArrayList<String> monthsList = PodcastParserMonths(yearIterator.previous());
            monthsList.forEach((element)->{
                try {
                    PodcastParserRecords(category, element);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private ArrayList<String> PodcastParserYears(Category category) throws InterruptedException, IOException {
        ArrayList<String> yearListString = new ArrayList<>(20);

        page = Jsoup.connect(category.getUrl()).get();

        /// Получение ссылки на последний год производится отдельно, так как ссылка на него отсутсвует
        /// в первоначальном списке
        String lastYear = page.select("h4").select("a[href]").last().attr("abs:href");
        page = Jsoup.connect(lastYear).get();
        yearListString.add(page.select("h4").select("a[href]").last().attr("abs:href"));
        yearListString.add(lastYear);

        ListIterator<String>yearIterator = yearListString.listIterator(1);

        while(yearIterator.hasNext()){
            String year = yearIterator.next();

            page = Jsoup.connect(year).timeout(10*1000).get();

            Elements bufElements = new Elements(page.select("h4").select("a[href]"));

            if(bufElements.size() <= 1){ break; }

            yearIterator.add(bufElements.first().attr("abs:href"));
            yearIterator.previous();

            TimeUnit.SECONDS.sleep(1);

            break;
            }
            return yearListString;
    }

    private ArrayList<String> PodcastParserMonths(String year) throws InterruptedException, IOException {
        ArrayList<String> monthsList = new ArrayList<>(12);

        page = Jsoup.connect(year).timeout(10 * 1000).get();
        page.select("dfn").select("a[href]").forEach((element -> {
            monthsList.add(element.attr("abs:href"));
        }));

        page = Jsoup.connect(monthsList.get(0)).get();
        monthsList.add(page.select("dfn").select("a[href]").last().attr("abs:href"));

        return monthsList;
    }

    private void PodcastParserRecords(Category category, String mounth) throws InterruptedException, IOException{
        page = Jsoup.connect(mounth).timeout(10 * 1000).get();
        Elements modeAudioItems = page.select("div.mod-audio-item");

        ListIterator<Element> modeAudioItemIterator = modeAudioItems.listIterator(modeAudioItems.size());
        while(modeAudioItemIterator.hasPrevious()){
            Element modeAudioItem = modeAudioItemIterator.previous();

            String title = modeAudioItem.select("h2.entry-title").first().ownText();
            String url = modeAudioItem.select("div.mod-audio-links").select("a[href]").first().attr("abs:href");
            Date date = PodcastParserRecordDate(modeAudioItem);

            logger.info("title: " + title);
            logger.info("date: " + date);
            logger.info("url: " + url);

            podcasts.add(new Podcast(0L, category.getId(), title, date, url));
        }
    }

    private Date PodcastParserRecordDate(Element mod_audio_item) {
        java.sql.Date dateSql = new Date(0L);

        DateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", russianMonth);
        try {
            String date = mod_audio_item.children().first().text();
            String newDate = String.format("%s %s %s", getDay(date), getMonth(date), getYear(date));
            java.util.Date dateUtil = dateFormat.parse(newDate);
            dateSql.setTime(dateUtil.getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
         return dateSql;
    }

    private String getDay(String date) {
        return date.split("[\\u00A0\\s]+")[0];
    }

    private String getMonth(String date) {
        return date.split("[\\u00A0\\s]+")[1];
    }

    private String getYear(String date) {
        return date.split("[\\u00A0\\s]+")[2];
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
