<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<c:set var="title"><op:translate key="SEARCH_FILTERS_HOME_SETTINGS_MODAL_TITLE"/></c:set>


<%--@elvariable id="optionsUrl" type="String"--%>
<button type="button" class="btn btn-link btn-link-hover-orange btn-sm text-secondary bg-white" data-target="#osivia-modal" data-load-url="${optionsUrl}" data-title="${title}">
    <i class="glyphicons glyphicons-basic-cogwheel"></i>
</button>
