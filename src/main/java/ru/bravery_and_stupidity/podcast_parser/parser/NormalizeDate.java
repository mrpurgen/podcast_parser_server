package ru.bravery_and_stupidity.podcast_parser.parser;

///FIXME: каким-то образом заменить на интерфейсы?
public class NormalizeDate {

    public String getNormalizeDate(String date){
        return String.format("%s %s %s", getDay(date), getMonth(date), getYear(date));
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
}
