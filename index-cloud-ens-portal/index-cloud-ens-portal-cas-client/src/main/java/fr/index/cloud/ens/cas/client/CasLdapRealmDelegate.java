package fr.index.cloud.ens.cas.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Name;

import org.jasig.cas.client.tomcat.CasRealm;
import org.jasig.cas.client.util.CommonUtils;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.model.Group;
import org.osivia.portal.api.directory.v2.service.GroupService;
import org.osivia.portal.core.constants.InternalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasLdapRealmDelegate implements CasRealm {



    private static final String MEMBERS = "members";

    private static final String UID = "uid=";


    private GroupService groupService = null;

    public GroupService getGroupService() {
        if (groupService == null)
            groupService = DirServiceFactory.getService(GroupService.class);
        return groupService;
    }


    /** {@inheritDoc} */
    @Override
    public Principal authenticate(final Principal p) {
        return p;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getRoles(final Principal p) {

        return new String[]{ MEMBERS};
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRole(final Principal principal, final String role) {
        
        if (MEMBERS.equals(role) && principal != null)
            return true;
        

        Group group = getGroupService().get(role);
        if( group == null)  {
            // redeployment of custom service ?
            groupService = null;
            group = getGroupService().get(role);
        }
        
        if (group != null) {
            List<Name> names = group.getMembers();
            for (Name name : names) {
                String cn = name.get(name.size() -1);
                if (cn.startsWith(UID)) {
                    String uid = cn.substring(UID.length());
                     
                    if (principal.getName().equals(uid)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }
}