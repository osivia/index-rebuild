package org.osivia.demo.userworkspaces;

import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;

import fr.toutatice.ecm.platform.core.userworkspace.ToutaticeUserWorkspaceServiceImpl;

public class UserWorkspaceService extends ToutaticeUserWorkspaceServiceImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6465767707468908495L;

	/**
	 * 
	 */
	private static final String TTC_TEMPLATE = "ttc:pageTemplate";
	/**
	 * 
	 */
	private static final String TTC_SHOW_IN_MENU = "ttc:showInMenu";
	/**
	 * 
	 */
	private static final String TTC_WEBID = "ttc:webid";
	/**
	 * 
	 */
	private static final String DC_TITLE = "dc:title";
	
	

    private static final Log log = LogFactory.getLog(UserWorkspaceService.class);


    /** Segments prefix size of UserWorkspace. */
    protected static final int UW_PREFIX_SEGMENT_SIZE = 3;

	
	protected void setFoldersACL(DocumentModel doc, String userName)
			throws ClientException {

		ACP acp = new ACPImpl();

		ACE grantMembersRead = new ACE(SecurityConstants.EVERYONE,
				SecurityConstants.READ, true);
		ACE grantEverything = new ACE(userName, SecurityConstants.EVERYTHING,
				true);
		ACL acl = new ACLImpl();
		acl.setACEs(new ACE[] { grantMembersRead, grantEverything });
		acp.addACL(acl);
		doc.setACP(acp, true);
	}

    /**
     * Gets UserWorkspace pathRef checking old policy first.
     * <ul>
     * <li>Actual UserWorkspace policy: /home/u/s/e/userlambda</li>
     * <li>Old UserWorkspace policy : /userworkspace-domain/userworkspaces/u/s/e/userlambda</li>
     * </ul>
     */
    @Override
    protected PathRef resolveUserWorkspace(CoreSession session, PathRef homeDomainRef, String username, String workspacename, int maxsize) {
        // UserWs path
        PathRef usWsRef = null;
        // Last segment
        String usWsName = super.getUserWorkspaceNameForUser(username);

    	Path usWsPath = computeUserWorspacePath(homeDomainRef.toString(), usWsName);
        // Ref
        usWsRef = new PathRef(usWsPath.toString());

        return usWsRef;
    }

    /**
     * Computes full UserWorkspace path.
     * 
     * @param rootPath
     * @param usWsName
     * @return full UserWorkspace Path
     */
    protected Path computeUserWorspacePath(String rootPath, String usWsName) {
        Path usWsPath = new Path("/" + rootPath);

        for (int i = 0; i < usWsName.length() && i < UW_PREFIX_SEGMENT_SIZE; i++) {
            usWsPath = usWsPath.append(usWsName.charAt(i) + "/");
        }
        usWsPath = usWsPath.append(usWsName);

        return usWsPath;
    }


    @Override
    protected DocumentModel doCreateUserWorkspace(CoreSession unrestrictedSession, PathRef userWSRef, Principal principal, String userName)
            throws ClientException {
        // User Workspace
        DocumentModel uw = null;
        Path uwsPath = new Path(userWSRef.toString());

        for (int i = UW_PREFIX_SEGMENT_SIZE - 1; i < uwsPath.segmentCount(); i++) {

            if (log.isDebugEnabled()) {
                log.debug("    - Création du document " + uwsPath.uptoSegment(i).toString());
            }

            PathRef nodeRef = new PathRef(uwsPath.uptoSegment(i).toString());
            if (!unrestrictedSession.exists(nodeRef)) {
                DocumentModel node = createUserWorkspacesRootSegment(unrestrictedSession, nodeRef);
                Validate.isTrue(node.getPathAsString().equals(nodeRef.toString()));
            }
        }
        // create user WS if needed
        if (!unrestrictedSession.exists(userWSRef)) {

            if (log.isDebugEnabled()) {
                log.debug(" -- Création du userWorkspace du user [" + userName + "] : " + userWSRef.value);
            }
            
            uw = super.doCreateUserWorkspace(unrestrictedSession, userWSRef, principal, userName);

        }
        //
		// Paramétrage affichage portail
		// userWorkspace.setProperty("toutatice", "pageTemplate",
		// "/templates/userWorkspace");
        uw.setProperty("toutatice", "tabOrder", "100");
        uw.setPropertyValue(DC_TITLE, "Mon espace");

		uw = unrestrictedSession.saveDocument(uw);
		
		String workspaceId = null;
		Serializable propertyValue = uw.getPropertyValue("webc:url");
		if(propertyValue != null) {
			workspaceId = propertyValue.toString();
		}

		// Initialisation Mes documents
		DocumentModel mesDocs = unrestrictedSession.createDocumentModel(
				uw.getPathAsString(), "documents", "Folder");
		mesDocs.setPropertyValue(DC_TITLE, "Documents");
		mesDocs.setPropertyValue(TTC_SHOW_IN_MENU, Boolean.TRUE);
		unrestrictedSession.createDocument(mesDocs);
		
		if(workspaceId != null) {
			// Initialisation Recherche
			DocumentModel searchStaple = unrestrictedSession.createDocumentModel(
					uw.getPathAsString(), "search-staple", "Staple");
			searchStaple.setPropertyValue(DC_TITLE, "Recherche");
			searchStaple.setPropertyValue(TTC_SHOW_IN_MENU, Boolean.TRUE);		
			searchStaple.setPropertyValue(TTC_TEMPLATE, "/default/templates/workspace/search");		
			searchStaple.setPropertyValue(TTC_WEBID, "workspace_"+workspaceId+"_search");		

			unrestrictedSession.createDocument(searchStaple);
		}
		

        return uw;
    }

    protected DocumentModel createUserWorkspacesRootSegment(CoreSession unrestrictedSession, PathRef rootRef) throws ClientException {

        String parentPath = new Path(rootRef.toString()).removeLastSegments(1).toString();
        String docName = new Path(rootRef.toString()).lastSegment();
        DocumentModel doc = unrestrictedSession.createDocumentModel(parentPath, docName, getUserWorkspaceRootType());
        doc.setProperty("dublincore", "title", docName);
        doc.setProperty("dublincore", "description", "");
        doc = unrestrictedSession.createDocument(doc);

        super.setUserWorkspaceRootACL(doc);

        return doc;
    }


}
