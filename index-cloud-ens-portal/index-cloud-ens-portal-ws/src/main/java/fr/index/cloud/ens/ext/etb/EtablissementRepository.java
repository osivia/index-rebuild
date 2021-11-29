package fr.index.cloud.ens.ext.etb;

import fr.index.cloud.ens.application.api.Application;


/**
 * The Interface EtablissementRepository.
 */
public interface EtablissementRepository {
    
    /**
     * Update.
     *
     * @param application the application
     */
    public void update( Application application);
    
    /**
     * Delete the application
     *
     * @param code the code
     */
    public void delete(  String code);
    
    /**
     * Gets the application.
     *
     * @param code the code
     * @return the application
     */
    public Application getApplication( String code);
}
