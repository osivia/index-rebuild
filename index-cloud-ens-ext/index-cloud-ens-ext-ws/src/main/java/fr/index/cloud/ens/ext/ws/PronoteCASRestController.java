package fr.index.cloud.ens.ext.ws;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Mock CAS PRONOTE
 * 
 * https://cloud-ens.index-education.local/pronote-ws/serviceValidate?ticket=[1/2]&service=URL
 * 
 * @author Jean-Sébastien
 */
@RestController
public class PronoteCASRestController {

    @RequestMapping(value = "/serviceValidate", method = RequestMethod.GET)
    public HttpEntity<byte[]>  serviceValidate(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "service", required = true) String service, @RequestParam(value = "ticket", required = true) String ticket) throws Exception {

        String user = "";
        if(!StringUtils.isEmpty(ticket))
            user = ticket;
        
        String xml="";
        
        if(user.length()> 0)
            xml = 
       " <cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n"

+      "  <cas:authenticationSuccess> \n"

+      "             <cas:user>"+user+"</cas:user>\n"

+      "        </cas:authenticationSuccess>\n"

+      "      </cas:serviceResponse>\n";
        else
            xml = 
                    "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'> \n"

+      "            <cas:authenticationFailure> code='INVALID_TICKET'>\n"

+      "            le ticket "+ticket+" est inconnu\n"

+      "            </cas:authenticationFailure>\n"

 +      "           </cas:serviceResponse>     \n"     ;  
        
        byte[] documentBody = xml.getBytes();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "xml"));
        header.setContentLength(documentBody.length);
        return new HttpEntity<byte[]>(documentBody, header);        

    }


}
