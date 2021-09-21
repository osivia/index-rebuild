package fr.index.cloud.ens.directory.person.card.portlet.model.validation;

import java.util.List;

import org.osivia.directory.v2.service.PersonUpdateService;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.api.directory.v2.service.PersonService;
import org.osivia.services.person.card.portlet.model.validator.PersonEditionFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import fr.index.cloud.ens.directory.person.card.portlet.model.CustomizedPersonEditionForm;




/**
 * Person edition customized form validator.
 *
 * @author Cédric Krommenhoek
 * @see PersonEditionFormValidator
 */
@Component
@Primary
public class CustomizedPersonEditionFormValidator extends PersonEditionFormValidator {

    
    @Autowired
    private PersonService personService;
    
    /**
     * Constructor.
     */
    public CustomizedPersonEditionFormValidator() {
        super();
    }


    @Override
    public void validate(Object target, Errors errors) {
        super.validate(target, errors);
        
        CustomizedPersonEditionForm form = (CustomizedPersonEditionForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "NotEmpty");
        
        
        // A person can be in directory 
        Person searchByMail = this.personService.getEmptyPerson();
        searchByMail.setMail(form.getMail());
        List<Person> findByCriteria = personService.findByCriteria(searchByMail);
        if (findByCriteria.size() > 1) {
            errors.rejectValue("mail", "alreadyactive"); 
         }  else if (findByCriteria.size() == 1) {
            Person personFound = findByCriteria.get(0);
            if (!personFound.getUid().equals(form.getUid()))  {
                errors.rejectValue("mail", "alreadyactive");
            }            
        }
        
        // A nick name must be unique
        Person searchByNickname = this.personService.getEmptyPerson();
        searchByNickname.setDisplayName(form.getNickname());
        List<Person> findByNickname = personService.findByCriteria(searchByNickname);
        
        if (findByNickname.size() > 1) {
            errors.rejectValue("nickname", "alreadyactive");
         }  else if (findByNickname.size() == 1) {
            Person personFound = findByNickname.get(0);
            if (!personFound.getUid().equals(form.getUid()))  {
                errors.rejectValue("nickname", "alreadyactive");
            }            
        }
 
        
    }

}
