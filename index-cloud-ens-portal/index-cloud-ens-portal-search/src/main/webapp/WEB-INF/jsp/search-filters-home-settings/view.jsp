<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="save" var="url"/>

<c:set var="namespace"><portlet:namespace/></c:set>

<c:set var="select2Searching"><op:translate key="SELECT2_SEARCHING"/></c:set>
<c:set var="select2NoResults"><op:translate key="SELECT2_NO_RESULTS"/></c:set>


<%--@elvariable id="form" type="fr.index.cloud.ens.search.filters.home.settings.portlet.model.SearchFiltersHomeSettingsForm"--%>
<form:form action="${url}" method="post" modelAttribute="form">
    <%--Form mode--%>
    <div class="form-check">
        <form:radiobutton id="${namespace}-mode-form" path="mode" cssClass="form-check-input" value="FORM"/>
        <form:label for="${namespace}-mode-form" path="mode">
            <strong><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_MODE_FORM"/></strong>
        </form:label>
    </div>

    <%--Levels--%>
    <portlet:resourceURL id="load-vocabulary" var="loadUrl">
        <portlet:param name="vocabulary" value="idx_level"/>
    </portlet:resourceURL>
    <c:set var="placeholder"><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_LEVEL_PLACEHOLDER"/></c:set>
    <div class="form-group ml-4">
        <form:label path="levels"><op:translate key="SEARCH_FILTERS_LEVEL_LABEL"/></form:label>
        <form:select path="levels" cssClass="form-control select2 select2-default" data-url="${loadUrl}" data-placeholder="${placeholder}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:forEach var="level" items="${form.levels}">
                <form:option value="${level}"><ttc:vocabularyLabel name="idx_level" key="${level}"/></form:option>
            </c:forEach>
        </form:select>
    </div>

    <%--Subjects--%>
    <portlet:resourceURL id="load-vocabulary" var="loadUrl">
        <portlet:param name="vocabulary" value="idx_subject"/>
    </portlet:resourceURL>
    <c:set var="placeholder"><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_SUBJECT_PLACEHOLDER"/></c:set>
    <div class="form-group ml-4">
        <form:label path="subjects"><op:translate key="SEARCH_FILTERS_SUBJECT_LABEL"/></form:label>
        <form:select path="subjects" cssClass="form-control select2 select2-default" data-url="${loadUrl}" data-placeholder="${placeholder}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:forEach var="subject" items="${form.subjects}">
                <form:option value="${subject}"><ttc:vocabularyLabel name="idx_subject" key="${subject}"/></form:option>
            </c:forEach>
        </form:select>
    </div>

    <%--Filter mode--%>
    <div class="form-check">
        <form:radiobutton id="${namespace}-mode-filter" path="mode" cssClass="form-check-input" value="FILTER"/>
        <form:label for="${namespace}-mode-filter" path="mode">
            <strong><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_MODE_FILTER"/></strong>
        </form:label>
    </div>

    <%--User saved search--%>
    <div class="form-group ml-4">
        <portlet:resourceURL id="load-saved-searches" var="loadUrl"/>
        <c:set var="placeholder"><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_SAVED_SEARCH_PLACEHOLDER"/></c:set>
        <form:label path="savedSearch"><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_SAVED_SEARCH_LABEL"/></form:label>
        <form:select path="savedSearch" cssClass="form-control select2 select2-default" data-url="${loadUrl}" data-placeholder="${placeholder}" data-searching="${select2Searching}" data-no-results="${select2NoResults}">
            <c:if test="${not empty form.savedSearch}">
                <form:option value="${form.savedSearch.id}">${form.savedSearch.displayName}</form:option>
            </c:if>
        </form:select>
    </div>

    <%--Buttons--%>
    <div class="text-right">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

        <button type="submit" class="btn btn-primary">
            <span><op:translate key="VALIDATE"/></span>
        </button>
    </div>
</form:form>
