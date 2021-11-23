package fr.index.cloud.ens.directory.person.deleting.portlet.model.validation;

import fr.index.cloud.ens.directory.person.deleting.portlet.model.PersonDeletingForm;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Person deleting form validator.
 *
 * @author Cédric Krommenhoek
 * @see Validator
 */
@Component
public class PersonDeletingFormValidator implements Validator {

    /** Person service. */
    @Autowired
    private PersonUpdateService personService;


    /**
     * Constructor.
     */
    public PersonDeletingFormValidator() {
        super();
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDeletingForm.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        PersonDeletingForm form = (PersonDeletingForm) target;

        // Mail
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        if (StringUtils.isNotBlank(form.getPassword())) {
            if (!this.personService.verifyPassword(form.getPerson().getUid(), form.getPassword())) {
                errors.rejectValue("password", "invalid");
            }
        }

        if (BooleanUtils.isNotTrue(form.getAccepted())) {
            errors.rejectValue("accepted", "empty");
        }
    }

}
