/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.controller;

import java.util.Date;

import org.nuxeo.ecm.automation.client.model.Document;

/**
 * @author Loïc Billon
 *
 */
public class Export {

	public enum ExportStatus {RUNNING, DONE};
	
	private Document pi; 
	
	private Date date;
	
	private ExportStatus status;
	
	private String zipFilename;

	/**
	 * @return the pi
	 */
	public Document getPi() {
		return pi;
	}

	/**
	 * @param pi the pi to set
	 */
	public void setPi(Document pi) {
		this.pi = pi;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public ExportStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ExportStatus status) {
		this.status = status;
	}

	/**
	 * @return the zipFilename
	 */
	public String getZipFilename() {
		return zipFilename;
	}

	/**
	 * @param zipFilename the zipFilename to set
	 */
	public void setZipFilename(String zipFilename) {
		this.zipFilename = zipFilename;
	}

	
}
