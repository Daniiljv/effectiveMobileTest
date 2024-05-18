package my.code.effectivemobiletest.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("application.properties")
public class DatabaseConfig {
    @Value("${spring.datasource.url}")
    String URL;
    @Value("${spring.datasource.username}")
    String USER;
    @Value("${spring.datasource.password}")
    String PASSWORD;

    @Bean
    public java.sql.Connection connection() {
        java.sql.Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
        return connection;
    }
}
