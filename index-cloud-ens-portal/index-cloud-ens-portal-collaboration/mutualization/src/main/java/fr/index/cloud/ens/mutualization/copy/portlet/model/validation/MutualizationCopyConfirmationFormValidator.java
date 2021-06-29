package fr.index.cloud.ens.mutualization.copy.portlet.model.validation;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyConfirmationForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Mutualization copy confirmation form validator.
 *
 * @author Cédric Krommenhoek
 * @see Validator
 */
@Component
public class MutualizationCopyConfirmationFormValidator implements Validator {

    /**
     * Constructor.
     */
    public MutualizationCopyConfirmationFormValidator() {
        super();
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return MutualizationCopyConfirmationForm.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "choice", "empty");
    }

}
