package config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Class for definition our db and hibernate properties
 */

@Configuration
public class HibernateConfig {

    /**
     * method of definition db properties
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
     * method of definition hibernate properties
     * @return hibernate properties
     */
    @Bean(value = "hibernateProps")
    Properties hibernateProps(){
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");

        //properties.put("hibernate.show_sql","true");
        return properties;
    }


}
