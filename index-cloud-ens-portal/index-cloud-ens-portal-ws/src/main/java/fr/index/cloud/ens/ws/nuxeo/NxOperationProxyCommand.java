package fr.index.cloud.ens.ws.nuxeo;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.Constants;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.model.DocRef;
import org.nuxeo.ecm.automation.client.model.OperationInput;
import org.nuxeo.ecm.automation.client.model.PropertyMap;

import fr.toutatice.portail.cms.nuxeo.api.INuxeoCommand;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilter;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoQueryFilterContext;
import net.sf.json.JSONObject;


/**
 * Generic Drive command proxy
 * 
 * @author Jean-Sébastien
 */
public class NxOperationProxyCommand implements INuxeoCommand {

    private String command;
    private String requestBody;
    private OperationInput input;
    private Map<String,String> parameters = null;
    private HttpServletRequest clientRequest;
    

    
    /**
     * Setter for parameters.
     * @param parameters the parameters to set
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    
    public void setOperationInput(OperationInput input) {

        this.input = input;
    }
    
    public NxOperationProxyCommand(String command, String requestBody) {
        super();
        this.command = command;
        this.requestBody = requestBody;

    }
    
    public NxOperationProxyCommand(String command, HttpServletRequest request, String requestBody) {
        super();
        this.command = command;
        this.clientRequest = request;
        this.requestBody = requestBody;

    }

    @Override
    public Object execute(Session nuxeoSession) throws Exception {

        // Operation request
        OperationRequest request = nuxeoSession.newRequest(this.command);
        
        // Copy client headers
        //[host, user-agent, accept-encoding, accept, content-type, x-nxproperties, x-nxdocumentproperties, x-nxrepository, x-application-name, x-client-version, x-user-id, x-device-id, cache-control, cookie, x-authentication-token, osivia-virtual-host, nuxeo-virtual-host, x-forwarded-proto, x-forwarded-for, x-forwarded-host, x-forwarded-server, connection, content-length, Accept]
        if( clientRequest != null)  {
            Enumeration<String> headers = clientRequest.getHeaderNames();
             while (headers.hasMoreElements()) {
                 String headerName = headers.nextElement();
                 if( headerName.startsWith("x-nx"))
                     request.getHeaders().put(headerName, clientRequest.getHeader(headerName));
            }
        }
        
        // Force blob download
        request.getHeaders().put("force-content-type", "application/json");
        
        if( requestBody != null)    {
            final JSONObject obj = JSONObject.fromObject(requestBody);
            if( obj != null)    {
                JSONObject params =  obj.getJSONObject("params");
        
                for( Object child : params.entrySet())  {
                    if( child instanceof AbstractMapEntry)  {
                        AbstractMapEntry entry = ((AbstractMapEntry) child);
                        request.getParameters().put((String)entry.getKey(), entry.getValue().toString());
                    }
                }
                String input =  obj.optString("input");
                
                if( StringUtils.isNotEmpty(input)) {
                    request.setInput(new DocRef(input));
                }

            }
            
            
        }
        
        if( parameters != null) {
            for (String key: parameters.keySet()) {
                request.getParameters().put(key, parameters.get(key));
            }
        }

        if(input != null)   {
            request.setInput(input);
        }

        return request.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("/"+command);
        builder.append("/"+requestBody);

        return builder.toString();
    }
}
