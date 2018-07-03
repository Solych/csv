package config;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Pavel on 08.05.2018.
 */
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "EMF",
        transactionManagerRef = "txManagerH2")
@EnableTransactionManagement
public class EmbeddedDbDataSource {
    @Primary
    @Bean(name = "embeddedDb")
    public DataSource dataSource() {
//        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//        EmbeddedDatabase db = builder
//                .setType(EmbeddedDatabaseType.H2)
//                .addScript("scripts/createTimeTable.sql")
//                .addScript("scripts/INSERT.sql")
//                .addScript("scripts/init.sql")
//                .build();
        return new EmbeddedDatabaseBuilder().setName("testdb;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=false")
                .setType(EmbeddedDatabaseType.H2)
                .addScript("scripts/createTimeTable.sql")
                .addScript("scripts/INSERT.sql")
                .addScript("scripts/init.sql")
                .build();

    }

    @Primary
    @Bean(value = "EMF")
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        entityManagerFactoryBean.setJpaProperties(hibernateProps());
        entityManagerFactoryBean.setPackagesToScan("model");
        return entityManagerFactoryBean;
    }


    /**
     * method of definition hibernate properties
     *
     * @return hibernate properties
     */
    @Primary
    @Bean
    Properties hibernateProps() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        //properties.put("hibernate.show_sql","true");
        return properties;

    }


    @Primary
    @Bean(name = "txManagerH2")
    JpaTransactionManager transactionManager() {
        JpaTransactionManager jtm = new JpaTransactionManager();
        jtm.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return jtm;
    }


    @PostConstruct
    public void startDbManager(){
        DatabaseManagerSwing.main(new String[] { "--url", "jdbc:h2:mem:testdb", "--user", "sa", "--password", "" });
    }

}

