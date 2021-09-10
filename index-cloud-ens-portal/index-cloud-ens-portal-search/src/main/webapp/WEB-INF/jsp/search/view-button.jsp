<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>



<portlet:actionURL var="url" name="search-new-filter"/>

<div class="d-flex justify-content-end">
    <a href="${url}" class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate">
        <strong><op:translate key="SEARCH_BUTTON_LABEL"/></strong>
    </a>
</div>
