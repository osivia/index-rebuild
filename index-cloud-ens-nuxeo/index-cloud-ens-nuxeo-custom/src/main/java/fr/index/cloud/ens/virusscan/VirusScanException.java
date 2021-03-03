package fr.index.cloud.ens.virusscan;

import org.nuxeo.ecm.core.api.RecoverableClientException;

public class VirusScanException extends RecoverableClientException{

    public VirusScanException(String message, String localizedMessage, String[] params) {
        super(message, localizedMessage, params);

    }

}
