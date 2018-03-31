package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

/**
 * Created by Pavel on 30.03.2018.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"repository"}, entityManagerFactoryRef = "EntityManagerFactory")
@EnableTransactionManagement
@Import(EntityManager.class)
@ComponentScan(basePackageClasses = config.EntityManager.class)
public class TransactionConfig {



    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory){
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(entityManagerFactory);
        return jtm;
    }
}
