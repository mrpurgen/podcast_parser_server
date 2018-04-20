package ru.bravery_and_stupidity.podcast_parser.repository.specification;


import org.jetbrains.annotations.NotNull;

public class SpecificationByIdRange implements SqlSpecification {
    @NotNull
    private long minId;

    @NotNull
    private long maxId;

    @NotNull
    private long categoryId;

    public SpecificationByIdRange(long categoryId, long minId, long maxId) {
        this.minId = minId;
        this.maxId = maxId;
        this.categoryId = categoryId;
    }

    @Override
    public String toSqlQuery() {
        return String.format("categoryId = %s and id between %s and %s", categoryId, minId, maxId);
    }
}
