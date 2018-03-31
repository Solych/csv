package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Pavel on 28.03.2018.
 */

@Configuration
public class HibernateConfig {

    /**
     *
     * @return db configuration
     */
    @Bean
    public DataSource restDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setPassword("wiecae9du");
        return dataSource;
    }

    /**
     *
     * @return hibernate properties
     */
    @Bean(value = "hibernateProps")
    Properties hibernateProps(){
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.show_sql","true");
        return properties;
    }


}
