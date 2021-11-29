package fr.index.security.oauth.approval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

import fr.index.cloud.oauth.tokenStore.AggregateRefreshTokenInfos;
import fr.index.cloud.oauth.tokenStore.IPortalTokenStore;
import fr.index.cloud.oauth.tokenStore.PortalRefreshTokenAuthenticationDatas;

/**
 * The Class PronoteApprovalStore.
 * 
 * Like in Google System, we consider that the approvals expiration is directly
 * linked to the refresh token expiration
 * 
 * Thanks to this implementation, if a user wants to renew a refresh token, he won't be
 * asked for consentement if he has already one active consentement 
 * 
 */
public class PronoteApprovalStore implements ApprovalStore {

    
    private IPortalTokenStore store;

    /**
     * @param store the token store to set
     */
    public void setPortalTokenStore(IPortalTokenStore store) {
        this.store = store;
    }
    
    @Override
    public boolean addApprovals(Collection<Approval> approvals) {
        return true;
    }

    @Override
    public boolean revokeApprovals(Collection<Approval> approvals) {
        return true;
    }

    @Override
    public Collection<Approval> getApprovals(String userId, String clientId) {
        Collection<Approval> result = new ArrayList<Approval>();
        Collection<AggregateRefreshTokenInfos> tokens = store.findTokensByUserName(userId);
        for (AggregateRefreshTokenInfos token : tokens) {
             PortalRefreshTokenAuthenticationDatas authentication = token.getAuthentication();

            if (authentication != null && authentication.getClientId().equals(clientId)) {
                Date expiresAt = token.getExpirationDate();
                
                if( expiresAt != null)  {
                    for (String scope : authentication.getScope()) {
                        result.add(new Approval(userId, clientId, scope, expiresAt, ApprovalStatus.APPROVED));
                    }
                }
            }
        }
        return result;
    }

}
