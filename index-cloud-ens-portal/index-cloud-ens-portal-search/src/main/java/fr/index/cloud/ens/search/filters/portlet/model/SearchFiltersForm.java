package fr.index.cloud.ens.search.filters.portlet.model;

import fr.toutatice.portail.cms.nuxeo.api.domain.DocumentDTO;

import org.osivia.portal.api.directory.v2.model.Person;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Search filters form java-bean.
 *
 * @author Cédric Krommenhoek
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchFiltersForm {

    /**
     * Location.
     */
    private DocumentDTO location;
    /**
     * View.
     */
    private SearchFiltersView view;

    /**
     * Keywords.
     */
    private String keywords;
    /**
     * Document types.
     */
    private List<String> documentTypes;
    /**
     * Levels.
     */
    private List<String> levels;
    /**
     * Subjects.
     */
    private List<String> subjects;
    /**
     * Location path.
     */
    private String locationPath;
    /**
     * Size range.
     */
    private SearchFiltersSizeRange sizeRange;
    /**
     * Size amount.
     */
    private Float sizeAmount;
    /**
     * Size unit.
     */
    private SearchFiltersSizeUnit sizeUnit;
    /**
     * Date range.
     */
    private SearchFiltersDateRange dateRange;
    /**
     * Customized date.
     */
    private Date customizedDate;

    /**
     * Saved search display name.
     */
    private String savedSearchDisplayName;
    
    /** The format. */
    private List<String> formats;
    
    /** The share. */
    private List<String> shareds;


    /** The share. */
    private List<CustomPerson> authors;



    




    /**
     * Constructor.
     */
    public SearchFiltersForm() {
        super();
    }


    public DocumentDTO getLocation() {
        return location;
    }

    public void setLocation(DocumentDTO location) {
        this.location = location;
    }

    public SearchFiltersView getView() {
        return view;
    }

    public void setView(SearchFiltersView view) {
        this.view = view;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<String> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<String> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public List<String> getLevels() {
        return levels;
    }

    public void setLevels(List<String> levels) {
        this.levels = levels;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }

    public SearchFiltersSizeRange getSizeRange() {
        return sizeRange;
    }

    public void setSizeRange(SearchFiltersSizeRange sizeRange) {
        this.sizeRange = sizeRange;
    }

    public Float getSizeAmount() {
        return sizeAmount;
    }

    public void setSizeAmount(Float sizeAmount) {
        this.sizeAmount = sizeAmount;
    }

    public SearchFiltersSizeUnit getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(SearchFiltersSizeUnit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public SearchFiltersDateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(SearchFiltersDateRange dateRange) {
        this.dateRange = dateRange;
    }

    public Date getCustomizedDate() {
        return customizedDate;
    }

    public void setCustomizedDate(Date customizedDate) {
        this.customizedDate = customizedDate;
    }

    public String getSavedSearchDisplayName() {
        return savedSearchDisplayName;
    }

    public void setSavedSearchDisplayName(String savedSearchDisplayName) {
        this.savedSearchDisplayName = savedSearchDisplayName;
    }
    
    /**
     * Getter for formats.
     * @return the formats
     */
    public List<String> getFormats() {
        return formats;
    }


    
    /**
     * Setter for formats.
     * @param formats the formats to set
     */
    public void setFormats(List<String> formats) {
        this.formats = formats;
    }


    
    /**
     * Getter for shareds.
     * @return the shareds
     */
    public List<String> getShareds() {
        return shareds;
    }


    
    /**
     * Setter for shareds.
     * @param shareds the shareds to set
     */
    public void setShareds(List<String> shareds) {
        this.shareds = shareds;
    }

    

    
    /**
     * Getter for authors.
     * @return the authors
     */
    public List<CustomPerson> getAuthors() {
        return authors;
    }


    
    /**
     * Setter for authors.
     * @param authors the authors to set
     */
    public void setAuthors(List<CustomPerson> authors) {
        this.authors = authors;
    }

}
