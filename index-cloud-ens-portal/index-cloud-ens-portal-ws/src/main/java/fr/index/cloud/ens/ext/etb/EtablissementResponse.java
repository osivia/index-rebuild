package fr.index.cloud.ens.ext.etb;

import org.osivia.portal.api.cache.services.IGlobalParameters;

/**
 * Données renvoyées par le web service pronote des établissements
 * 
 * @author Jean-Sébastien
 */
public class EtablissementResponse  {
  
     
    
    /** nom de l'établissement **/   
    private String nom;    
 
    public EtablissementResponse()  {
        super();
    }
    
    public EtablissementResponse(String nom) {
        super();
        this.nom = nom;
    }


    /**
     * Getter for nom.
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    
    /**
     * Setter for nom.
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }
    

    
}
