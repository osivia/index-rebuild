package fr.index.cloud.ens.application.card;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Application edition form validator.
 * 
 * @author Jean-Sébastien Steux
 * @see Validator
 */
@Component
public class ApplicationCardEditionFormValidator implements Validator {

    /**
     * Constructor.
     */
    public ApplicationCardEditionFormValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ApplicationCardForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bean.title", "NotEmpty");
    }

}
