/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.controller;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author Loïc Billon
 *
 */
@Component
public class RenewPasswordInitFormValidator implements Validator {
	
	
    /** Mail regex. */
    private static final String MAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /** Mail pattern. */
    private final Pattern mailPattern;
    
    @Autowired
    private PersonUpdateService personService;
    
    /**
	 * 
	 */
	public RenewPasswordInitFormValidator() {
		
        super();

        // Mail pattern
        this.mailPattern = Pattern.compile(MAIL_REGEX);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return RenewPasswordForm.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		RenewPasswordForm form = (RenewPasswordForm) target;
		
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "empty");
        
        if(!errors.hasErrors()) {
            Matcher matcher = this.mailPattern.matcher(StringUtils.trim(form.getMail()));
            if (!matcher.matches()) {
                errors.rejectValue("mail", "invalid");

            }
        }

        if(!errors.hasErrors()) {
	        String mail = form.getMail();
	        
	        Person searchByMail = personService.getEmptyPerson();
	        searchByMail.setMail(mail);
	        List<Person> list = personService.findByCriteria(searchByMail);
	        if(list.size() != 1 ) {
	        	errors.rejectValue("mail", "unknown");
	        }
	        else {
	        	Person person = list.get(0);
	        	
	        	// Account expired
	        	if(person.getValidity() != null && person.getValidity().before(new Date())) {
	            	errors.rejectValue("mail", "unknown");
	        	}
	        }
        }
	}

}
