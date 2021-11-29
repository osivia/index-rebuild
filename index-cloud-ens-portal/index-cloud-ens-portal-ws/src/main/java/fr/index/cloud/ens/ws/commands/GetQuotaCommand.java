package fr.index.cloud.ens.ws.commands;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.Blob;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.PathRef;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;


/**
 * Get Quota Nuxeo command.
 * 
 * @author Jean-Sébastien
 * @see INuxeoCommand
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GetQuotaCommand implements INuxeoCommand {

	/** Document path */
	private final String path;

	/**
	 * Constructor.
	 * 
	 * @param basePath
	 *            
	 */
	
    /** Log. */
    private final Log log;
    
	public GetQuotaCommand(String path) {
		super();
		this.path = path;
		
        
        this.log = LogFactory.getLog(this.getClass());   		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(Session nuxeoSession) throws Exception {
        

        DocRef workspace = new PathRef(path);
		OperationRequest request = nuxeoSession.newRequest("Quota.GetInfo");
		request.setInput(workspace);

		
		Object quota = request.execute();

		return quota;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getName());
		builder.append("/");
		builder.append(this.path);
		return builder.toString();
	}

}
