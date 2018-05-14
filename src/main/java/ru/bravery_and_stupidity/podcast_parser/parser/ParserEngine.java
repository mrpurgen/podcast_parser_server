package ru.bravery_and_stupidity.podcast_parser.parser;

import java.io.IOException;
import java.util.List;

public interface ParserEngine<T, E> {
   WrapObject getPage(String url) throws IOException;
   List<T> getContentFromPage(E page, String nameTag, String nameClass);
   List<T> getContentFromPage(E page, String nameTag);
   List<T> getContentFromElement(T element, String nameTag);
   List<T> getContentFromElement(T element, String nameTag, String nameClass);
   List<T> getContentFromElement(List<T> classElements, String nameTag, String nameClass);
   List<T> getLinks(List<T> classElements);
   String getOwnText(T elememt);
   String getAttr(T element, String nameAttribut);
   List<T> getChildren(T element);
   String getText(T element);
}
