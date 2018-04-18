package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Definition entityManagerFactory
 */
@Configuration
@Transactional
//@Import(HibernateConfig.class)
public class EntityManager {

    /**
     * Hibernate properties
     */
    @Autowired
    @Qualifier("hibernateProps")
    Properties props;

    /**
     * setting our db and hibernate properties on entityManagerFactory
     * Hibernate jpa vendor adapter - supplier of hibernate (REQUIRED!)
     */
    @Bean(value = "EntityManagerFactoryBean")
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(props);
        entityManagerFactoryBean.setPackagesToScan("model");
        return entityManagerFactoryBean;
    }


}
