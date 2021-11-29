package fr.index.cloud.oauth.authentication;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import fr.index.cloud.ens.ws.DriveRestController;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;

@Component
public class PortalAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    @Qualifier("personUpdateService")
    private PersonUpdateService personUpdateService;
    
    
    @Autowired
    PortalUserDetailService userDetailService; 

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        String uid = name; 
        boolean found = true;
        try {
            if( name.contains("@")) {
                Person criteria = personUpdateService.getEmptyPerson();
                criteria.setMail(name);
               
                List<Person> persons = personUpdateService.findByCriteria(criteria);
                if( persons.size() == 1)    {
                    
                    Person person = persons.get(0);
                    if( person.getValidity() != null) {
                    
                    // if portalPersonExternal = FALSE, portalPersonValidity should be after current day
                        if (person.getValidity().before(new Date())) {
                            throw new FailedLoginException("Account has expired");
                        }

                    }
                    
                    uid = persons.get(0).getUid();
                }
            }
            
            found = personUpdateService.verifyPassword(uid, password);
        } catch (Exception e) {
            found = false;
        }
        if (!found)
            throw new BadCredentialsException("Bad user/password !");
        
        UserDetails user = userDetailService.loadUserByUsername(uid);
        
        return new PortalAuthentication(uid, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if (authentication.equals(UsernamePasswordAuthenticationToken.class)) {
            return true;
        }

        return authentication.equals(PortalAuthentication.class);
    }
}