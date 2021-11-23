/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Loïc Billon
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PersonExportForm {

	public PersonExportForm() {
        super();
    }

    private Map<String, Export> exports = new HashMap<>();
	
	private Boolean isExportRunning = Boolean.FALSE;
	
	private Boolean limitReached = Boolean.FALSE;

	/**
	 * @return the exports
	 */
	public Map<String, Export> getExports() {
		return exports;
	}

	/**
	 * @param exports the exports to set
	 */
	public void setExports(Map<String, Export> exports) {
		this.exports = exports;
	}

	/**
	 * @return the isExportRunning
	 */
	public Boolean getIsExportRunning() {
		return isExportRunning;
	}

	/**
	 * @param isExportRunning the isExportRunning to set
	 */
	public void setIsExportRunning(Boolean isExportRunning) {
		this.isExportRunning = isExportRunning;
	}

	/**
	 * @return the limitReached
	 */
	public Boolean getLimitReached() {
		return limitReached;
	}

	/**
	 * @param limitReached the limitReached to set
	 */
	public void setLimitReached(Boolean limitReached) {
		this.limitReached = limitReached;
	}

}
