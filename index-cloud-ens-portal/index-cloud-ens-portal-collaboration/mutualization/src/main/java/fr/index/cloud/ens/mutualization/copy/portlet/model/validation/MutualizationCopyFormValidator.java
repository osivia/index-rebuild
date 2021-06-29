package fr.index.cloud.ens.mutualization.copy.portlet.model.validation;

import fr.index.cloud.ens.mutualization.copy.portlet.model.MutualizationCopyForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Mutualization copy form validator.
 *
 * @author Cédric Krommenhoek
 * @see Validator
 */
@Component
public class MutualizationCopyFormValidator implements Validator {

    /**
     * Constructor.
     */
    public MutualizationCopyFormValidator() {
        super();
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return MutualizationCopyForm.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "targetPath", "empty");
    }

}
