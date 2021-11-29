/*
 * (C) Copyright 2014 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *
 *
 */
package fr.index.cloud.ens.ws.commands;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.osivia.portal.api.cms.Symlink;
import org.osivia.portal.api.cms.Symlinks;
import org.osivia.portal.api.cms.VirtualNavigationUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.NavigationItem;

import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import fr.toutatice.portail.cms.nuxeo.api.domain.INavigationAdapterModule;



/**
 * Search items.
 *
 * @author jeanseb
 * @see INuxeoCommand
 */
public class SearchCommand implements INuxeoCommand {

    /** Default navigation schemas. */
    private static final String TREE_SCHEMAS = "dublincore, common, toutatice, file, resourceSharing";


    /** Parent path. */
    private final String basePath;

    /** The type. */
    private final String type;
    
    /** The mime type. */
    private final String mimeType;


    public SearchCommand(String basePath, String type, String mimeType) {
        this.basePath = basePath;
        this.type = type;
        this.mimeType = mimeType;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Session session) throws Exception {
      

        // Operation request
        OperationRequest operationRequest = session.newRequest("Document.Query");

        // Nuxeo query clause
        String clause = "((ecm:path STARTSWITH '"+basePath + "/') OR (ecm:path = '"+basePath+"'))";
        
       
        if( StringUtils.isNotEmpty(type))   {
            if( DriveRestController.DRIVE_TYPE_FOLDER.equals(type)) {
                clause  = clause + " AND ( ecm:primaryType = 'Folder' )";
            }
            else if( DriveRestController.DRIVE_TYPE_FILE.equals(type))  {
                clause  = clause + " AND ( ecm:mixinType <> 'Folderish' )";                
            }
        }   
        
        if( StringUtils.isNotEmpty(mimeType))   {
            clause  = clause + " AND (file:content/mime-type='"+mimeType+"' ) ";
        }
        
          

        // Nuxeo query filter
        NuxeoQueryFilterContext filter = NuxeoQueryFilterContext.CONTEXT_LIVE;


        // Apply Nuxeo query clause filter
        String filteredClause = NuxeoQueryFilter.addPublicationFilter(filter, clause.toString());


        // Nuxeo query
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM Document WHERE ");
        query.append(filteredClause);
        query.append(" ORDER BY ecm:path");
        operationRequest.set("query", query.toString());

        // Navigation schemas
        StringBuilder treeSchemas = new StringBuilder();
        treeSchemas.append(TREE_SCHEMAS);

        operationRequest.setHeader(Constants.HEADER_NX_SCHEMAS, treeSchemas.toString());

        // Operation execution
        Documents children = (Documents) operationRequest.execute();

        // tree items
        Map<String, Document> treeItems = new LinkedHashMap<>(children.size());

        for (Document child : children) {
            // Path
            treeItems.put(child.getPath(), child);
        }


        return treeItems;
    }


   

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("|");
        builder.append(this.basePath);
        return builder.toString();
    };

}
