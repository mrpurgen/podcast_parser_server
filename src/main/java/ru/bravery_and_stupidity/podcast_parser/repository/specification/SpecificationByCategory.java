package ru.bravery_and_stupidity.podcast_parser.repository.specification;

public class SpecificationByCategory implements SqlSpecification {
    private Long categoryId;

    public SpecificationByCategory(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toSqlQuery() {
        return String.format("categoryId = %s", categoryId);
    }
}
