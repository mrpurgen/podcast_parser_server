package ru.bravery_and_stupidity.podcast_parser.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@Profile("test")
@EnableTransactionManagement
public class Config {
//    @Bean(name = "datasource")
//    public DriverManagerDataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
//        dataSource.setUrl("jdbc:hsqldb:mem:mydb");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("jdbc:hsqldb:mem:mydb");
//        return dataSource;
//    }
    @Bean(name = "datasource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/parserdb");
        dataSource.setUsername("root");
        dataSource.setPassword("gsogsoegzxrf123");
        return dataSource;
    }
}



