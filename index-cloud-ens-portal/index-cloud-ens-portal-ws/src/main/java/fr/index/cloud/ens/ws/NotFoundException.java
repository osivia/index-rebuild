package fr.index.cloud.ens.ws;


/**
 * The Class NotFoundException.
 */
public class NotFoundException extends GenericException {

    public NotFoundException(String searchIdentifier) {
        super(null, searchIdentifier);

    }

}
