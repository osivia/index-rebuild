package fr.index.cloud.ens.filebrowser.portlet.controller;

import fr.index.cloud.ens.filebrowser.commons.portlet.controller.AbstractFileBrowserController;
import org.osivia.services.workspace.filebrowser.portlet.controller.FileBrowserController;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * File browser customized portlet controller.
 *
 * @author Cédric Krommenhoek
 * @see AbstractFileBrowserController
 */
@Controller
@Primary
@RequestMapping("VIEW")
public class CustomizedFileBrowserController extends AbstractFileBrowserController {

    /**
     * Constructor.
     */
    public CustomizedFileBrowserController() {
        super();
    }

}
