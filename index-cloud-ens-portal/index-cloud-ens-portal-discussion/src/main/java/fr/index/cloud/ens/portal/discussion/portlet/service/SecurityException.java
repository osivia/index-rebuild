package fr.index.cloud.ens.portal.discussion.portlet.service;

import javax.portlet.PortletException;

public class SecurityException extends PortletException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -8333967041825984838L;

    public SecurityException(String message)    {
        super(message);
    }

}
