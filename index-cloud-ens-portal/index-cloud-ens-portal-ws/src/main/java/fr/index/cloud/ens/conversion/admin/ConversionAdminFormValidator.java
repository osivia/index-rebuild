package fr.index.cloud.ens.conversion.admin;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Conversion admin form validator.
 * 
 * @author Jean-Sébastien Steux
 * @see Validator
 */
@Component
public class ConversionAdminFormValidator implements Validator {

    /**
     * Constructor.
     */
    public ConversionAdminFormValidator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return ConversionAdminForm.class.isAssignableFrom(clazz);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object target, Errors errors) {
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bean.title", "NotEmpty");
    }

}
