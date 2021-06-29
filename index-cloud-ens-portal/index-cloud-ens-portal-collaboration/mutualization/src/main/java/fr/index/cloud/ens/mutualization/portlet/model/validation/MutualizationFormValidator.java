package fr.index.cloud.ens.mutualization.portlet.model.validation;

import fr.index.cloud.ens.mutualization.portlet.model.MutualizationForm;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Mutualization form validator.
 *
 * @author Cédric Krommenhoek
 * @see Validator
 */
@Component
public class MutualizationFormValidator implements Validator {

    /**
     * Constructor.
     */
    public MutualizationFormValidator() {
        super();
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return MutualizationForm.class.isAssignableFrom(clazz);
    }


    @Override
    public void validate(Object target, Errors errors) {
        // Form
        MutualizationForm form = (MutualizationForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "empty");

        if (CollectionUtils.isEmpty(form.getKeywords())) {
            errors.rejectValue("keywords", "empty");
        }

        if (CollectionUtils.isEmpty(form.getDocumentTypes())) {
            errors.rejectValue("documentTypes", "empty");
        }

        if (CollectionUtils.isEmpty(form.getLevels())) {
            errors.rejectValue("levels", "empty");
        }

        if (CollectionUtils.isEmpty(form.getSubjects())) {
            errors.rejectValue("subjects", "empty");
        }
        
        form.setSuggestedKeywords(form.getKeywords());
    }

}
