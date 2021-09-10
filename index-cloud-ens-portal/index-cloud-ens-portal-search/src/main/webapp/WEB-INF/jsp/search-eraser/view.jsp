<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL var="url" name="reset"/>

<div class="d-flex justify-content-end">
    <a href="${url}" class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate">
        <i class="glyphicons glyphicons-basic-reload"></i>
        <strong><op:translate key="SEARCH_RESET"/></strong>
    </a>
</div>
