package fr.index.cloud.ens.directory.model.preferences;

/**
 * Customized file browser column interface.
 *
 * @author Cédric Krommenhoek
 */
public interface CustomizedFileBrowserColumn {

    /**
     * Get column identifier.
     *
     * @return identifier
     */
    
    String getId();


    /**
     * Get column order.
     *
     * @return order
     */
    int getOrder();


    /**
     * Set column order.
     *
     * @param order order
     */
    void setOrder(int order);


    /**
     * Check if column is visible.
     *
     * @return true if column is visible
     */
    boolean isVisible();


    /**
     * Set column visibility indicator
     *
     * @param visible visibility indicator
     */
    void setVisible(boolean visible);

}
