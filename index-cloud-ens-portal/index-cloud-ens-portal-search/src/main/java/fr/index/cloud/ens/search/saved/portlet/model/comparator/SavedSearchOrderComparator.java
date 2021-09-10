package fr.index.cloud.ens.search.saved.portlet.model.comparator;

import org.osivia.directory.v2.model.preferences.UserSavedSearch;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Saved search order comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see UserSavedSearch
 */
@Component
public class SavedSearchOrderComparator implements Comparator<UserSavedSearch> {

    /**
     * Constructor.
     */
    public SavedSearchOrderComparator() {
        super();
    }


    @Override
    public int compare(UserSavedSearch savedSearch1, UserSavedSearch savedSearch2) {
        int result;

        if (savedSearch1 == null) {
            result = -1;
        } else if (savedSearch2 == null) {
            result = 1;
        } else if (savedSearch1.getOrder() == null) {
            if (savedSearch2.getOrder() == null) {
                // Identifier comparison
                result = Integer.compare(savedSearch1.getId(), savedSearch2.getId());
            } else {
                result = 1;
            }
        } else if (savedSearch2.getOrder() == null) {
            result = -1;
        } else {
            // Order comparison
            result = Integer.compare(savedSearch1.getOrder(), savedSearch2.getOrder());
        }

        return result;
    }

}
