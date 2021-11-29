package fr.index.cloud.ens.application.api;


/**
 * Service de récupération des informations établissement
 * 
 * @author Jean-Sébastien
 */
public interface IApplicationService {
  
    
    /**
     * Renvoie en fonction du code de l'établissement les informations associées
     * à ce dernier
     * 
     * Ces informations sont basées sur un Web-Service PRONOTE
     * 
     * @param code identifiant de l'application pronote
     * @return
     */
    
    public Application getApplication(String code) ;
    
    public Application getApplicationByClientID(String clientID) ;
    
    public Application createByClientID(String clientID) ;
    
    public void update(Application application) ;
    
    public void deleteApplication(String code) ;
}
