package fr.index.cloud.ens.application.creation;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.index.cloud.ens.application.api.IApplicationService;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilter;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterContext;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterException;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterExecutor;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterParameterType;

/**
 * Application creation form filter.
 * 
 * @author Jean-Sébastien Steux
 * @see FormFilter
 */
@Component
public class ApplicationCreationFormFilter implements FormFilter {

    /** Form filter identifier. */
    public static final String IDENTIFIER = "APPLICATION_CREATION_FROM_PRONOTE";

    /** Form filter label internationalization key. */
    private static final String LABEL_INTERNATIONALIZATION_KEY = "APPLICATION_CREATION_FORM_FILTER_LABEL";
    /** Form filter description internationalization key. */
    private static final String DESCRIPTION_INTERNATIONALIZATION_KEY = "APPLICATION_CREATION_FORM_FILTER_DESCRIPTION";

    /** The Constant application code name */
    private static final String FIELD_CODEAPPLICATION = "codeapplication";


    /** Plugin service. */
    @Autowired
    private IApplicationService service;

    /** Internationalization bundle factory. */
    @Autowired
    private IBundleFactory bundleFactory;


    /**
     * Constructor.
     */
    public ApplicationCreationFormFilter() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return IDENTIFIER;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelKey() {
        return LABEL_INTERNATIONALIZATION_KEY;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescriptionKey() {
        return DESCRIPTION_INTERNATIONALIZATION_KEY;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, FormFilterParameterType> getParameters() {

        Map<String, FormFilterParameterType> parameters = new HashMap<>();
        return parameters;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren() {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(FormFilterContext context, FormFilterExecutor executor) throws FormFilterException {

        // HTTP servlet request
        HttpServletRequest servletRequest = context.getPortalControllerContext().getHttpServletRequest();

        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(servletRequest.getLocale());

        String codeApplication = context.getVariables().get(FIELD_CODEAPPLICATION).trim();

        if (this.service.createByClientID(codeApplication) == null) {
            String message = bundle.getString("APPLICATION_CREATION_FORM_FILTER_MESSAGE_ERROR_NOT_EXISTS");
            throw new FormFilterException(message);
        }


    }
}