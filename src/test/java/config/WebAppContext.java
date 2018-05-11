package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;



@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"service", "controller", "repository", "model", "config"})
public class WebAppContext extends WebMvcConfigurationSupport {
}
