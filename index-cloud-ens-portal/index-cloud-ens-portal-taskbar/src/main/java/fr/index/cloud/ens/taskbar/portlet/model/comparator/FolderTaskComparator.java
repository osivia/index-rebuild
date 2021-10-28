package fr.index.cloud.ens.taskbar.portlet.model.comparator;

import fr.index.cloud.ens.taskbar.portlet.model.FolderTask;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Folder task comparator.
 *
 * @author Cédric Krommenhoek
 * @see Comparator
 * @see FolderTask
 */
@Component
public class FolderTaskComparator implements Comparator<FolderTask> {

    /**
     * Constructor.
     */
    public FolderTaskComparator() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(FolderTask folder1, FolderTask folder2) {
        int result;

        if (!BooleanUtils.xor(new boolean[]{folder1.isFolder(), folder2.isFolder()})) {
            result = folder1.getDisplayName().compareToIgnoreCase(folder2.getDisplayName());
        } else if (folder1.isFolder()) {
            result = -1;
        } else {
            result = 1;
        }

        return result;
    }

}
