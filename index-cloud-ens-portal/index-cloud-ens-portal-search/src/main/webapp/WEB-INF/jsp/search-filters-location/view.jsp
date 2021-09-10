<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:defineObjects/>


<portlet:actionURL name="select" var="selectUrl"/>

<portlet:resourceURL id="browse" var="browseUrl">
    <portlet:param name="cmsBasePath" value="${form.basePath}"/>
    <portlet:param name="navigation" value="true"/>
    <portlet:param name="live" value="true"/>
    <portlet:param name="includedTypes" value="Folder"/>
    <portlet:param name="hideUnavailable" value="true"/>
</portlet:resourceURL>


<%--@elvariable id="form" type="fr.index.cloud.ens.search.filters.location.portlet.model.SearchFiltersLocationForm"--%>
<form:form action="${selectUrl}" method="post" modelAttribute="form" role="form">
    <%--Target path--%>
    <div class="form-group selector">
        <form:label path="targetPath"><op:translate key="SEARCH_FILTERS_LOCATION_LABEL"/></form:label>
        <form:hidden path="targetPath" cssClass="selector-value form-control"/>
        <div class="fancytree fancytree-selector fixed-height p-2 border rounded" data-lazyloadingurl="${browseUrl}"></div>
    </div>

    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="saveSearchFiltersLocation(this)">
            <span><op:translate key="SEARCH_FILTERS_LOCATION_SELECT"/></span>
        </button>
    </div>
</form:form>
