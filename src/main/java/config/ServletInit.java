package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Class for definition our web-application with spring mvc
 * Required overriding 3 functions
 */
public class ServletInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * Show root classes such a repositoryConfig, securityConfig
     * @return root classes of web-application
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {
                WebAppImpl.class
        };
    }

    /**
     * Show class, which represents configuration of dispatcher servlet
     * @return class, where define dispatcher servlet
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {
                WebAppImpl.class
        };
    }

    /**
     * Occurs connection with dispatcher servlet
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }





}
