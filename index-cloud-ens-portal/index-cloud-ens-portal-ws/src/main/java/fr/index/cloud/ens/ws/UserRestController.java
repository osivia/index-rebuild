package fr.index.cloud.ens.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.tokens.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.interfaces.DecodedJWT;


/**
 * Services rest associés aux users
 * @author Loïc Billon
 */
@RestController
public class UserRestController {

    /**
     * 
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    public static PortletContext portletContext;


    @Autowired
    @Qualifier("personUpdateService")
    private PersonUpdateService personUpdateService;

    @Autowired
    private ITokenService tokenService;
    
    @Autowired
    private ErrorMgr errorMgr;

    /** Logger. */
    private static final Log logger = LogFactory.getLog(UserRestController.class);


    /**
     * La création du compte est effectuée depuis un lien ‘Créer un compte’ de PRONOTE.
     * Authentification du jeton JWT + création du portalToken avec les informations transmises dans le contenu du jeton.
     * 
     * @param request
     * @param response
     * @return url de création de compte, code d'erreur éventuel.
     * @throws Exception
     */
    @RequestMapping(value = "/User.signup", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> signUp(HttpServletRequest request, HttpServletResponse response) {


        Map<String, Object> returnObject = new LinkedHashMap<>();
        
        try {
            
            // 1 - decode the jwt token, check mandatory fields and initialize a map of attributs.
            Map<String, String> attributes = decodeRequest(request, returnObject);
            
            if(returnObject.isEmpty()) {
                
                // 2 - check if a user is already registered
                checkValidAccount(attributes, returnObject); 
                
            }
            
            if(returnObject.isEmpty()) {

                // 3 - Create the portal token
                String webToken = tokenService.generateToken(attributes);
    
                // 4 - Compute and return a link to start UserCreation procedure
                //computeCreateAccountProcUrl(request, returnObject, webToken);
                computeCreateAccountUrl(request, returnObject, webToken);
                returnObject.put("returnCode", ErrorMgr.ERR_OK);
            }
            
            
        }
        catch(Exception e) {
            WSPortalControllerContext wsCtx = new WSPortalControllerContext(request, response);

            returnObject = errorMgr.handleDefaultExceptions(wsCtx, e);
        }
        
        return returnObject;

    }
    





	/**
     * decode the jwt token, check mandatory fields and initialize a map of attributs. 
     * 
     * @param request
     * @param returnObject
     * @return
     * @throws PortalException
     */
    protected Map<String, String> decodeRequest(HttpServletRequest request, Map<String, Object> returnObject) throws PortalException {

        Map<String, String> attributes = new ConcurrentHashMap<String, String>();
        
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(token) && (token.startsWith(TOKEN_PREFIX))) {
   
            String issuer = System.getProperty("pronote.issuer");
            String pronoteSecret = System.getProperty("pronote.oauth2.jwt.secret");            
           
            DecodedJWT jwt = null;
            

            try {
                String publickeyPath = System.getProperty("pronote.oauth2.jwt.publicKey.path");

                if (StringUtils.isNotEmpty(publickeyPath)) {
                    File file = new File(publickeyPath);
                    if (file.exists()) {
                        PemReader pemReader = new PemReader(new InputStreamReader(new FileInputStream(publickeyPath)));
                        PemObject pemObject = pemReader.readPemObject();
                        pemReader.close();

                        byte[] content = pemObject.getContent();
                        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);

                        KeyFactory factory = KeyFactory.getInstance("RSA");
                        RSAKey key = (RSAKey) factory.generatePublic(pubKeySpec);

                        jwt = JWT.require(Algorithm.RSA256(key)).build().verify(token.substring(TOKEN_PREFIX.length()));
                   }
                }
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                errorMgr.addErrorResponse(returnObject, 2, "Error during RSA based verification, token cannot be verified : " + e.getMessage());
            } catch (AlgorithmMismatchException e) {
                if (StringUtils.isEmpty(pronoteSecret)) {
                    errorMgr.addErrorResponse(returnObject, 2, "Error during RSA based verification : " + e.getMessage());
                }
            }
            
            if (jwt == null) {
                // try secret Key
                if (StringUtils.isNotEmpty(pronoteSecret)) {
                    try {
                        Algorithm algorithm = Algorithm.HMAC256(pronoteSecret);

                        JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance
                        jwt = verifier.verify(token.substring(TOKEN_PREFIX.length()));
                    } catch (Exception e) {
                        errorMgr.addErrorResponse(returnObject, 2, "Error during HMAC256 based verification : : " + e.getMessage());
                    }
                }
            }

            if (jwt != null) {
            	
				// Accept blank / absent values
				String firstName = jwt.getClaim("firstName").asString();
				String lastName = jwt.getClaim("lastName").asString();
				String mail = jwt.getClaim("mail").asString();

				if (StringUtils.isEmpty(firstName))
					firstName = "";

				if (StringUtils.isEmpty(lastName))
					lastName = "";

				if (StringUtils.isEmpty(mail))
					mail = "";

				attributes.put("firstname", firstName);
				attributes.put("lastname", lastName);
				attributes.put("mail", mail);

            }
            else {
                throw new PortalException("JWT token is empty.");
            }
        }
        else {
            errorMgr.addErrorResponse(returnObject, 2, "Token is mandatory and is empty");

        }
        
        return attributes;
        
    }
    
    /**
     * check if a user is already registered
     * @param attributes
     * @param returnObject
     */
    private void checkValidAccount(Map<String, String> attributes, Map<String, Object> returnObject) {
        
        Person searchedPerson = personUpdateService.getEmptyPerson();
        searchedPerson.setMail(attributes.get("mail"));
        List<Person> search = personUpdateService.findByCriteria(searchedPerson);


        if (search.size() > 0 && search.get(0).getLastConnection() != null) {

            errorMgr.addErrorResponse(returnObject, 1, "A valid user account is registered with this mail.");

        }
    }
    
    /**
	 * @param request
	 * @param returnObject
	 * @param webToken
	 */
	private void computeCreateAccountUrl(HttpServletRequest request, Map<String, Object> returnObject,
			String webToken) {
		
        String url = "/portal/portal/default/create-account/";

        String publicHost = System.getProperty("osivia.tasks.host");
        url = publicHost + url + "?init=true&token=" + webToken;
        returnObject.put("url", url);
		
	}

    
    /**
     * Gets the user profile
     *
     * @param request the request
     * @param response the response
     * @param principal the principal
     * @return the web url
     * @throws Exception the exception
     */
    
    @RequestMapping(value = "/User.getProfile", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)

    public Map<String, Object> getUserProfile(HttpServletRequest request, HttpServletResponse response, 
            Principal principal) throws Exception {

        Map<String, Object> returnObject = new LinkedHashMap<>();
        returnObject.put("returnCode", ErrorMgr.ERR_OK);

        WSPortalControllerContext ctx = new WSPortalControllerContext(request, response, principal);

        try {
            Person findPerson = personUpdateService.getEmptyPerson();
            findPerson.setUid(principal.getName());
            List<Person> users = personUpdateService.findByCriteria(findPerson);
            if (users.size() != 1) {
                throw new Exception("User not found : " + users.size());
            }
            Person user = users.get(0);
        	
             returnObject.put("id", user.getUid());
             returnObject.put("mail", user.getMail());
             returnObject.put("firstName", user.getGivenName());
             returnObject.put("lastName", user.getSn());             
 
        } catch (Exception e) {
            returnObject = errorMgr.handleDefaultExceptions(ctx, e);
        }
        return returnObject;
    }

}
