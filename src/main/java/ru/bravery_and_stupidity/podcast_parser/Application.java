package ru.bravery_and_stupidity.podcast_parser;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.bravery_and_stupidity.podcast_parser.parser.Parser;

@SpringBootApplication
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

//    @Autowired
//    Parser parser;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(Application.class, args);

        logger.info("podcast parser is running");
        Parser parser = new Parser();
    }

}
