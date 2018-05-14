package ru.bravery_and_stupidity.podcast_parser.parser;

import java.util.Collections;

public class WrapObject<T> {
    private T object;
    public WrapObject(T object){
        this.object = object;
    }

    public T getObject() {
        return object;
    }
}
