package ru.bravery_and_stupidity.podcast_parser.repository.specification;

import java.sql.Date;

public class SpecificationByDateRange implements SqlSpecification {
    private Date minDate;
    private Date maxDate;

    public SpecificationByDateRange(Date minDate, Date maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    @Override
    public String toSqlQuery() {
        return String.format("date between '%s' and '%s'", minDate, maxDate);
    }
}
