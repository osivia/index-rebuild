package fr.index.cloud.ens.directory.person.card.portlet.service;

import fr.index.cloud.ens.directory.person.card.portlet.model.CustomizedPersonEditionForm;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.services.person.card.portlet.model.PersonEditionForm;
import org.osivia.services.person.card.portlet.service.PersonCardServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.portlet.PortletException;

/**
 * Person card customized portlet service implementation.
 *
 * @author Cédric Krommenhoek
 * @see PersonCardServiceImpl
 */
@Service
@Primary
public class CustomizedPersonCardServiceImpl extends PersonCardServiceImpl {

    /**
     * Constructor.
     */
    public CustomizedPersonCardServiceImpl() {
        super();
    }


    @Override
    protected void fillLdapProperties(PortalControllerContext portalControllerContext, PersonEditionForm form, Person person) throws PortletException {
        super.fillLdapProperties(portalControllerContext, form, person);

        if (form instanceof CustomizedPersonEditionForm) {
            // Customized form
            CustomizedPersonEditionForm customizedForm = (CustomizedPersonEditionForm) form;

            // Nickname
            String nickname = person.getDisplayName();
            customizedForm.setNickname(nickname);
            
            // Uid
            String uid = person.getUid();
            customizedForm.setUid(uid);
        }
    }


    @Override
    protected void setLdapProperties(PortalControllerContext portalControllerContext, PersonEditionForm form, Person person) throws PortletException {
        super.setLdapProperties(portalControllerContext, form, person);

        if (form instanceof CustomizedPersonEditionForm) {
            // Customized form
            CustomizedPersonEditionForm customizedForm = (CustomizedPersonEditionForm) form;

            // Nickname
            String nickname = customizedForm.getNickname();
            person.setDisplayName(nickname);

            // CN
            String cn = customizedForm.getFirstName() + " " + customizedForm.getLastName();
            person.setCn(cn);
        }
    }

}
