package ru.bravery_and_stupidity.podcast_parser.parser;

import java.text.DateFormatSymbols;

public class RussianDateFormatSymbols extends DateFormatSymbols {
    public RussianDateFormatSymbols(){
        super();
    }

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
}
