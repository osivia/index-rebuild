package fr.index.cloud.ens.portal.discussion.portlet.model.comparator;


import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionsFormSort;
import fr.index.cloud.ens.portal.discussion.portlet.model.DiscussionDocument;
import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;

/**
 * Discussion comparator.
 *
 * @author Jean-Sébastien Steux
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DiscussionComparator implements Comparator<DiscussionDocument> {

    /**
     * Comparator sort property.
     */
    private final DiscussionsFormSort sort;
    /**
     * Comparator alternative sort indicator.
     */
    private final boolean alt;


    /**
     * Constructor.
     *
     * @param sort sort property
     * @param alt alternative sort indicator
     */
    public DiscussionComparator(DiscussionsFormSort sort, boolean alt) {
        super();
        this.sort = sort;
        this.alt = alt;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(DiscussionDocument document1, DiscussionDocument document2) {
        int result;

        if (document1 == null) {
            result = -1;
        } else if (document2 == null) {
            result = 1;
        } else if (DiscussionsFormSort.DATE.equals(this.sort)) {
            // Date can be null (ie. no messages)
            if (document1.getLastModified() == null) {
                if (document2.getLastModified() == null)
                    result = 0;
                else
                    result = -1;
            } else {
                if (document2.getLastModified() == null)
                    result = 1;
                else
                    result = document1.getLastModified().compareTo(document2.getLastModified());
            }
        } else {
            // Title can be null (ie. no participant)
            if (document1.getTitle() == null) {
                if (document1.getTitle() == null)
                    result = 0;
                else
                    result = -1;
            } else {
                if (document2.getTitle() == null)
                    result = 1;
                else
                    result = document1.getTitle().compareTo(document2.getTitle());
            }

        }

        if (this.alt) {
            result = -result;
        }


        return result;
    }


}
