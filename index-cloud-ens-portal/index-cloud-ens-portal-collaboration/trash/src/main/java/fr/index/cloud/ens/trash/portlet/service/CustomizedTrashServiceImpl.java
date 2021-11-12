package fr.index.cloud.ens.trash.portlet.service;

import org.dom4j.Element;
import org.osivia.portal.api.html.DOM4JUtils;
import org.osivia.services.workspace.portlet.service.TrashService;
import org.osivia.services.workspace.portlet.service.TrashServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Customized trash portlet service implementation.
 * @author Cédric Krommenhoek
 * @see TrashServiceImpl
 * @see TrashService
 */
@Service
@Primary
public class CustomizedTrashServiceImpl extends TrashServiceImpl implements TrashService {

    /**
     * Constructor.
     */
    public CustomizedTrashServiceImpl() {
        super();
    }


    @Override
    protected Element getToolbarButton(String id, String title, String icon) {
        // URL
        String url = "#" + id;
        // HTML classes
        String htmlClass = "btn btn-link btn-link-hover-primary text-primary-dark btn-sm mr-1 no-ajax-link";

        // Button
        Element button = DOM4JUtils.generateLinkElement(url, null, null, htmlClass, null, icon);
        DOM4JUtils.addDataAttribute(button, "toggle", "modal");

        // Text
        Element text = DOM4JUtils.generateElement("span", "d-none d-lg-inline", title);
        button.add(text);

        return button;
    }

}
