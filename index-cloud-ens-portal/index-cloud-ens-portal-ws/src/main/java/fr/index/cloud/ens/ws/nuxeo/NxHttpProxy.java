package fr.index.cloud.ens.ws.nuxeo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoSatelliteConnectionProperties;

public class NxHttpProxy {

    private String uri;

    public NxHttpProxy(String uri) {
        super();
        this.uri = uri;
    }

    public String execute() throws ClientProtocolException, IOException {
        String baseUrl = NuxeoSatelliteConnectionProperties.getConnectionProperties(null).getPrivateBaseUri().toString();


        String url = baseUrl + uri;

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet getRequest = new HttpGet(url);

        /*
        String params = "";
        Enumeration<String> paramsEnumeration = request.getParameterNames();
        while (paramsEnumeration.hasMoreElements()) {
            String name = (String) paramsEnumeration.nextElement();
            if( params.length() == 0)
                params += "?";
            else
                params += "&";
            params += name +"="+ URLEncoder.encode(request.getParameter(name),"UTF-8");
        }
        uri+=params;
        */
        
        
/*
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            getRequest.addHeader(name, request.getHeader(name));
        }
*/        
   
        // Send the request; It will immediately return the response in HttpResponse object
        org.apache.http.HttpResponse serverResponse = httpClient.execute(getRequest);

        // verify the valid error code first
        int statusCode = serverResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
        }

        // Now pull back the response object
        HttpEntity httpEntity = serverResponse.getEntity();
        String apiOutput = EntityUtils.toString(httpEntity);

        return apiOutput;
    }

}
