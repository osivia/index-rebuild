/**
 * 
 */
package fr.index.cloud.ens.directory.person.renew.portlet.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import fr.index.cloud.ens.directory.person.renew.portlet.service.RenewPasswordService;

/**
 * @author Loïc Billon
 *
 */
@Component
public class RenewPasswordFinalFormValidator implements Validator {

    @Autowired
    private RenewPasswordService service;
    
    /**
	 * 
	 */
	public RenewPasswordFinalFormValidator() {
		
        super();

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
		
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newpassword", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password2", "empty");

        if (StringUtils.isNotBlank(form.getNewpassword()) && StringUtils.isNotBlank(form.getPassword2())
                && !StringUtils.equals(form.getNewpassword(), form.getPassword2())) {
            errors.rejectValue("password2", "invalid");
        }
        
        service.validatePasswordRules(errors, "newpassword", form.getNewpassword());
	}

}
