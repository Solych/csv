package config;

import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.MultipartConfig;

/**
 * Created by Pavel on 29.03.2018.
 */
@Configuration

@EnableWebMvc
@ComponentScan(basePackages = {"config","controller","model", "repository","service"})
public class StartApp implements WebApplicationInitializer {

    private int MAX_UPLOAD_SIZE = 5*1024*1024;

    public void onStartup(ServletContext context) throws ServletException {
        AnnotationConfigWebApplicationContext container  = new AnnotationConfigWebApplicationContext();
        container.register(AppInit.class);
        container.setServletContext(context);
        ServletRegistration.Dynamic servlet = context.addServlet("servlet", new DispatcherServlet(container));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }

    /**
     * Function for defining view resolver
     * setSuffix - Which types of pages can show
     * setPrefix - Where this pages can be
     * @return
     */
    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".jsp");
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setViewClass(JstlView.class);
        return viewResolver;
    }
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(1000000);
        return commonsMultipartResolver;
    }


}
