package ru.bravery_and_stupidity.podcast_parser.repository.specification;


import org.jetbrains.annotations.NotNull;

public class SpecificationByIdRange implements SqlSpecification {
    @NotNull
    private long minId;

    @NotNull
    private long maxId;

    public SpecificationByIdRange(long minId, long maxId) {
        this.minId = minId;
        this.maxId = maxId;
    }

    @Override
    public String toSqlQuery() {
        return String.format("id between %s and %s", minId, maxId);
    }
}
