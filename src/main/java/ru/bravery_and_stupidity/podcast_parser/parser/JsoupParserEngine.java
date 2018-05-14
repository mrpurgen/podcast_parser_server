package ru.bravery_and_stupidity.podcast_parser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class JsoupParserEngine implements ParserEngine<Element, Document> {
    @Override
    public WrapObject getPage(String url) throws IOException{
        Document doc = Jsoup.connect(url).timeout(10000).get();
        return new WrapObject(doc);
    }

    @Override
    public List<Element> getContentFromPage(Document page, String nameTag, String nameClass) {
        String cssQuery = String.format("%s.%s", nameTag, nameClass);

        return page.select(cssQuery);
    }

    @Override
    public List<Element> getContentFromPage(Document page, String nameTag) {
        String cssQuery = String.format("%s", nameTag);
        return page.select(cssQuery);
    }

    @Override
    public List<Element> getContentFromElement(Element element, String nameTag) {
        String cssQuery = String.format("%s", nameTag);
        return element.select(cssQuery);
    }

    @Override
    public List<Element> getContentFromElement(Element element, String nameTag, String nameClass) {
        String cssQuery = String.format("%s.%s", nameTag, nameClass);

        return element.select(cssQuery);
    }

    @Override
    public List<Element> getContentFromElement(List<Element> classElements, String nameTag, String nameClass) {
        Elements elements = new Elements(classElements);
        String cssQuery = String.format("%s.%s", nameTag, nameClass);

        return elements.select(cssQuery);
    }

    @Override
    public List<Element> getLinks(List<Element> classElements) {
        Elements elements = new Elements(classElements);

        return elements.select("a[href]");
    }

    @Override
    public String getOwnText(Element element) {
        return element.ownText();
    }

    @Override
    public String getAttr(Element element, String nameAttribut) {
        return element.attr(nameAttribut);
    }

    @Override
    public List<Element> getChildren(Element element) {
        return element.children();
    }

    @Override
    public String getText(Element element) {
        return element.text();
    }
}
