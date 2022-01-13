package fr.index.cloud.ens.cas.client;



import org.apache.catalina.LifecycleException;
import org.jasig.cas.client.tomcat.CasRealm;
import org.jasig.cas.client.tomcat.PropertiesCasRealmDelegate;
import org.jasig.cas.client.tomcat.v85.AbstractCasRealm;

/**
 * Tomcat <code>Realm</code> that implements {@link CasRealm} backed by properties file
 * containing usernames/and roles of the following format:
 * <pre>
 * username1=role1,role2,role3
 * username2=role1
 * username3=role2,role3
 * </pre>
 * User authentication succeeds if the name of the given principal exists as
 * a username in the properties file.
 *
 * @author Marvin S. Addison
 * @version $Revision$
 * @since 3.1.12
 *
 */
public class CasLdapRealm extends AbstractCasRealm {

    private final CasLdapRealmDelegate delegate = new CasLdapRealmDelegate();



    /** {@inheritDoc} */
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();

    }

    /** {@inheritDoc} */
    @Override
    protected CasRealm getDelegate() {
        return this.delegate;
    }

}

