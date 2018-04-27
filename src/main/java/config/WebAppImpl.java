package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Class for definition dispatcher servlet
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"config","controller","model", "repository","service"})
public class WebAppImpl implements WebApplicationInitializer {

    /**
     * Default implementation of dispatcher servlet
     *
     * @param context
     * @throws ServletException
     */
    public void onStartup(ServletContext context) throws ServletException {
        AnnotationConfigWebApplicationContext container  = new AnnotationConfigWebApplicationContext();
        container.register(ServletInit.class);
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

    /**
     * Configuration for multipartFile
     * setMaxUploadSize - 10MB
     * @return
     */
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(100000000);
        return commonsMultipartResolver;
    }


}
