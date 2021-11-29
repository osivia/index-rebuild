package fr.index.cloud.ens.dashboard;

import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import org.nuxeo.ecm.automation.client.model.Document;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;

/**
 * Trashed document comparator.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardApplicationComparator implements Comparator<DashboardApplication> {

    /**
     * Comparator sort property.
     */
    private final DashboardSort sort;
    /**
     * Comparator alternative sort indicator.
     */
    private final boolean alt;


    /**
     * Constructor.
     *
     * @param sort sort property
     * @param alt  alternative sort indicator
     */
    public DashboardApplicationComparator(DashboardSort sort, boolean alt) {
        super();
        this.sort = sort;
        this.alt = alt;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(DashboardApplication application1, DashboardApplication application2) {
        int result;

        if (application1 == null) {
            result = -1;
        } else if (application2 == null) {
            result = 1;
        } else {
            // Application
            String title1 = application1.getClientName();
            String title2 = application2.getClientName();
            result = title1.compareToIgnoreCase(title2);
        }

        if (this.alt) {
            result = -result;
        }

        return result;
    }

}
