package fr.index.cloud.ens.portal.discussion.util;

import org.springframework.context.ApplicationContext;

/**
 * Application context provider.
 * 
 * @author Cédric Krommenhoek
 */
public class ApplicationContextProvider {

    /** Application context. */
    private static ApplicationContext applicationContext;


    /**
     * Private constructor, prevent instanciation.
     */
    private ApplicationContextProvider() {
        // Do nothing
    }


    /**
     * Getter for applicationContext.
     * 
     * @return the applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Setter for applicationContext.
     * 
     * @param applicationContext the applicationContext to set
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

}
