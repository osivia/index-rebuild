package fr.index.cloud.ens.dashboard;

import java.util.Date;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.index.cloud.oauth.tokenStore.AggregateRefreshTokenInfos;
import fr.index.cloud.oauth.tokenStore.PortalRefreshTokenAuthenticationDatas;

/**
 * Dashboard datas java-bean.
 * 
 * @author JS Steux
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardApplication  {


    /** Client identifier. */
    private String clientId;

    /** The client name. */
    private String clientName;


    /**
     * Constructor.
     * 
     */
    public DashboardApplication() {
        super();
    }


 
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

}
