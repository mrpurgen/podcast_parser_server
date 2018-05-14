package ru.bravery_and_stupidity.podcast_parser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.bravery_and_stupidity.podcast_parser.parser.JsoupParserEngine;
import ru.bravery_and_stupidity.podcast_parser.parser.Parser;

@Configuration
@Profile("deploy")
@EnableTransactionManagement
public class ConfigDeploy {
    @Bean(name = "datasource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/parserdb?useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("gsogsoegzxrf123");
        return dataSource;
    }

    @Bean(name = "parser")
    public Parser parser(){
        Parser parser = new Parser(new JsoupParserEngine());
        return parser;
    }
}
