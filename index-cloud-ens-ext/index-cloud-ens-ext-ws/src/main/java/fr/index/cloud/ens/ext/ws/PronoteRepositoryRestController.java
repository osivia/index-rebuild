package fr.index.cloud.ens.ext.ws;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Services Rest PRONOTE
 * 
 * @author Jean-Sébastien
 */
@RestController
public class PronoteRepositoryRestController {


    @RequestMapping(value = "/Pronote.getEtablissement", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> getWebUrl(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id", required = false) String type,
            @RequestParam(value = "id", required = false) String id, Principal principal) throws Exception {


        Map<String, Object> returnObject = new LinkedHashMap();

        try {

            returnObject.put("nom",  id);
            
        } catch (Exception e) {
            returnObject.put("errorCode", 999);
        }
        return returnObject;
    }


}
