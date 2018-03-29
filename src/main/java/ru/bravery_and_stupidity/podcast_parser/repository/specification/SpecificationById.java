package ru.bravery_and_stupidity.podcast_parser.repository.specification;

public class SpecificationById implements SqlSpecification {
    private Long id;

    public SpecificationById(Long id) {
        this.id = id;
    }

    @Override
    public String toSqlQuery() {
        return String.format("id = %s", id);
    }
}
