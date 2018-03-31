package ru.bravery_and_stupidity.podcast_parser.model.validator;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.bravery_and_stupidity.podcast_parser.model.Category;
import ru.bravery_and_stupidity.podcast_parser.model.Podcast;

@Component
public class ModelValidator {
    private static String NOT_NULL_MSG = "Parameter must not be null";
    private static String NOT_EMPTY_MSG = "Parameter must not be empty string";
    private static String NEGATIVE_ID = "Id must be positive";

    public static void validate(@Nullable Category item) throws IllegalStateException, IllegalArgumentException {
        Assert.notNull(item, NOT_NULL_MSG);
        Assert.state(item.getId() >= 0, NEGATIVE_ID);
        Assert.hasLength(item.getName().trim(), NOT_EMPTY_MSG);
        Assert.hasLength(item.getUrl().trim(), NOT_EMPTY_MSG);
    }

    public static void validate(@Nullable Podcast item) throws IllegalStateException, IllegalArgumentException {
        Assert.notNull(item, NOT_NULL_MSG);
        Assert.state(item.getId() >= 0, NEGATIVE_ID);
        Assert.state(item.getCategoryId() > 0, NEGATIVE_ID);
        Assert.hasLength(item.getName().trim(), NOT_EMPTY_MSG);
        Assert.hasLength(item.getUrl().trim(), NOT_EMPTY_MSG);
    }
}
