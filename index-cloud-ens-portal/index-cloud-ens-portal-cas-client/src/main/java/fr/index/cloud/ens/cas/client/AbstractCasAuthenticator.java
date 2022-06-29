package fr.index.cloud.ens.cas.client;



/**
 * Base class for all CAS protocol authenticators.
 * 
 * 
 * External SSO : this class hes been redefined to support external SSO
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1.12
 */
public abstract class AbstractCasAuthenticator extends AbstractAuthenticator {

    private String proxyCallbackUrl;

    protected final String getProxyCallbackUrl() {
        return this.proxyCallbackUrl;
    }

    public final void setProxyCallbackUrl(final String proxyCallbackUrl) {
        this.proxyCallbackUrl = proxyCallbackUrl;
    }

    @Override
    protected final String getArtifactParameterName() {
        return "ticket";
    }

    @Override
    protected final String getServiceParameterName() {
        return "service";
    }


}
