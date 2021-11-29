package fr.index.cloud.ens.application.management;

import org.springframework.context.ApplicationContext;

/**
 * Application context provider.
 * 
 * @author Jean-Sébastien Steux
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
