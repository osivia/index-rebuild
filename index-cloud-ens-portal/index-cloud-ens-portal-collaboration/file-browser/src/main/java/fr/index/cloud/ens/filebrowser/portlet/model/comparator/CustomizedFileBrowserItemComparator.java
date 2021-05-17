package fr.index.cloud.ens.filebrowser.portlet.model.comparator;

import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserItem;
import fr.index.cloud.ens.filebrowser.portlet.model.CustomizedFileBrowserSortEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserItem;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortCriteria;
import org.osivia.services.workspace.filebrowser.portlet.model.FileBrowserSortEnum;
import org.osivia.services.workspace.filebrowser.portlet.model.comparator.FileBrowserItemComparator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * File browser customized item comparator.
 *
 * @author Cédric Krommenhoek
 * @see FileBrowserItemComparator
 */
@Component
@Primary
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CustomizedFileBrowserItemComparator extends FileBrowserItemComparator {

    /** Sort criteria. */
    private final FileBrowserSortCriteria criteria;


    /**
     * Constructor.
     *
     * @param criteria sort criteria
     */
    public CustomizedFileBrowserItemComparator(FileBrowserSortCriteria criteria) {
        super(criteria);
        this.criteria = criteria;
    }


    @Override
    public int compare(FileBrowserItem item1, FileBrowserItem item2) {
        int result;

        // Customized comparison indicator
        boolean customizedComparison;

        if (!(item1 instanceof CustomizedFileBrowserItem) || !(item2 instanceof CustomizedFileBrowserItem)) {
            result = super.compare(item1, item2);
            customizedComparison = false;
        } else {
            CustomizedFileBrowserItem customizedItem1 = (CustomizedFileBrowserItem) item1;
            CustomizedFileBrowserItem customizedItem2 = (CustomizedFileBrowserItem) item2;

            if (CustomizedFileBrowserSortEnum.DOCUMENT_TYPE.equals(this.criteria.getField())) {
                result = this.compareLists(customizedItem1.getDocumentTypes(), customizedItem2.getDocumentTypes());
                customizedComparison = true;
            } else if (CustomizedFileBrowserSortEnum.LEVEL.equals(this.criteria.getField())) {
                result = this.compareLists(customizedItem1.getLevels(), customizedItem2.getLevels());
                customizedComparison = true;
            } else if (CustomizedFileBrowserSortEnum.SUBJECT.equals(this.criteria.getField())) {
                result = this.compareLists(customizedItem1.getSubjects(), customizedItem2.getSubjects());
                customizedComparison = true;
            } else if (CustomizedFileBrowserSortEnum.FORMAT.equals(this.criteria.getField())) {
                if( customizedItem1.getFormat() == null)
                    result = -1;
                else if (customizedItem2.getFormat() == null)
                    result =  1;
                else
                    result = customizedItem1.getFormat().compareTo(customizedItem2.getFormat());
                customizedComparison = true;
            } 
            else {
                result = super.compare(item1, item2);
                customizedComparison = false;
            }
        }

        // Alternative sort
        if (this.criteria.isAlt() && customizedComparison) {
            result = -result;
        }

        return result;
    }


    /**
     * Compare lists.
     * @param list1 list #1
     * @param list2 list #2
     * @return comparison result
     */
    private int compareLists(List<String> list1, List<String> list2) {
        int result;
        if (CollectionUtils.isEmpty(list1)) {
            result = -1;
        } else if (CollectionUtils.isEmpty(list2)) {
            result = 1;
        } else {
            result = 0;
            int i = 0;

            while ((result == 0) && (i < Math.min(list1.size(), list2.size()))) {
                String listItem1 = StringUtils.trimToEmpty(list1.get(i));
                String listItem2 = StringUtils.trimToEmpty(list2.get(i));

                result = listItem1.compareToIgnoreCase(listItem2);

                i++;
            }

            if (result == 0) {
                result = list1.size() - list2.size();
            }
        }
        return result;
    }
}
