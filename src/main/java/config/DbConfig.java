package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Pavel on 23.04.2018.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"repository"}, entityManagerFactoryRef = "EntityManagerFactoryBean",
        transactionManagerRef = "txManager")
@EnableTransactionManagement
public class DbConfig {


    /**
     * setting our db and hibernate properties on entityManagerFactory
     * Hibernate jpa vendor adapter - supplier of hibernate (REQUIRED!)
     */
    @Bean(value = "EntityManagerFactoryBean")
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(restDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(hibernateProps());
        entityManagerFactoryBean.setPackagesToScan("model");
        return entityManagerFactoryBean;
    }

    /**
     * method of definition db properties
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
     * method of definition hibernate properties
     *
     * @return hibernate properties
     */

    @Bean
    Properties hibernateProps() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        //properties.put("hibernate.show_sql","true");
        return properties;
    }


    @Bean(name = "txManager")
    JpaTransactionManager transactionManager() {
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return jtm;
    }

}
