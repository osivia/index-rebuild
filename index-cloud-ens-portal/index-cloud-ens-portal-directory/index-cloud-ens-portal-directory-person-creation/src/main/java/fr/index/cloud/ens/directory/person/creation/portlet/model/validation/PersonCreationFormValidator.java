/**
 *
 */
package fr.index.cloud.ens.directory.person.creation.portlet.model.validation;

import fr.index.cloud.ens.directory.person.creation.portlet.model.PersonCreationForm;
import fr.index.cloud.ens.directory.person.creation.portlet.service.PersonCreationService;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Loïc Billon
 *
 */
@Component
public class PersonCreationFormValidator implements Validator {

    /**
     * regex for first and lastname. Must fit alpha characters and spaces, quotes or
     * hyphens
     */
    private static final String NAME_REGEX = "^[a-zA-Z-' ]*$";
    /** regex for mails */
    private static final String MAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    /** Name pattern. */
    private final Pattern namePattern;
    /** Mail pattern. */
    private final Pattern mailPattern;
    @Autowired
    private PersonCreationService service;
    @Autowired
    private PersonUpdateService personService;

    /**
     *
     */
    public PersonCreationFormValidator() {

        super();

        // Name pattern
        this.namePattern = Pattern.compile(NAME_REGEX);
        // Mail pattern
        this.mailPattern = Pattern.compile(MAIL_REGEX);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return PersonCreationForm.class.isAssignableFrom(clazz);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.validation.Validator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */
    @Override
    public void validate(Object target, Errors errors) {
        PersonCreationForm form = (PersonCreationForm) target;

        // Empty fields
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newpassword", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmpassword", "empty");

        // correct whitespaces
        form.setFirstname(form.getFirstname().trim());
        form.setLastname(form.getLastname().trim());
        form.setMail(form.getMail().trim());

        // Check first and last name syntax
        if (StringUtils.isNotBlank(form.getFirstname())) {
            String cleanFirstname = service.removeAccents(form.getFirstname());
            Matcher matcher = this.namePattern.matcher(cleanFirstname);
            if (!matcher.matches()) {
                errors.rejectValue("firstname", "invalid");
            }
        }
        if (StringUtils.isNotBlank(form.getLastname())) {
            String cleanLastname = service.removeAccents(form.getLastname());
            Matcher matcher = this.namePattern.matcher(cleanLastname);
            if (!matcher.matches()) {
                errors.rejectValue("lastname", "invalid");
            }
        }

        // Check mail syntax
        if (StringUtils.isNotBlank(form.getMail())) {
            Matcher matcher = this.mailPattern.matcher(form.getMail());
            if (!matcher.matches()) {
                errors.rejectValue("mail", "invalid");
            }
        }

        // A person can be in directory but his account should not be valid
        Person searchByMail = this.personService.getEmptyPerson();
        searchByMail.setMail(form.getMail());
        List<Person> findByCriteria = personService.findByCriteria(searchByMail);
        if (findByCriteria.size() > 0) {
            Person personFound = findByCriteria.get(0);
            if (personFound.getValidity() == null) {
                errors.rejectValue("mail", "alreadyactive");
            }
        }
        
        // A nick name must be unique
        Person searchByNickname = this.personService.getEmptyPerson();
        searchByNickname.setDisplayName(form.getNickname());
        List<Person> findByNickname = personService.findByCriteria(searchByNickname);
        if (findByNickname.size() > 0) {
            boolean duplicateNickName = false;
            
            for(Person personFound:findByNickname)  {
                
                // Exclude pending creations 
                if (personFound.getValidity() == null) {         
                    duplicateNickName = true;
                }
            }
            
            if( duplicateNickName) {
                errors.rejectValue("nickname", "alreadyactive");
            }
        }

        if (StringUtils.isNotBlank(form.getNewpassword()) && StringUtils.isNotBlank(form.getConfirmpassword())
                && !StringUtils.equals(form.getNewpassword(), form.getConfirmpassword())) {
            errors.rejectValue("confirmpassword", "invalid");
        }

        service.validatePasswordRules(errors, "newpassword", form.getNewpassword());

        if (BooleanUtils.isNotTrue(form.getAcceptTermsOfService())) {
            errors.rejectValue("acceptTermsOfService", "required");
        }
    }

}
