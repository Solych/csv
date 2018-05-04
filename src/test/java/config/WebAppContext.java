package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Created by Pavel on 28.04.2018.
 */


@Configuration
@EnableWebMvc
@ComponentScan({"service", "controller", "repository", "config", "model"})
public class WebAppContext extends WebMvcConfigurationSupport {
}
