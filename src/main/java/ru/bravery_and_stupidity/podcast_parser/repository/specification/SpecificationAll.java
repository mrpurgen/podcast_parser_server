package ru.bravery_and_stupidity.podcast_parser.repository.specification;

public class SpecificationAll implements SqlSpecification {
    @Override
    public String toSqlQuery() {
        return "id > 0";
    }
}
