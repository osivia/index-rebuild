<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL var="searchUrl" name="search"/>
<portlet:actionURL var="resetUrl" name="reset"/>


<div class="d-flex text-nowrap  mt-3 mb-2">
    <%--Advanced search--%>
    <div class="d-flex justify-content-end mb-2 mr-2">
        <a href="${searchUrl}" class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate no-ajax-link">
            <i class="glyphicons glyphicons-basic-plus"></i>
            <strong><op:translate key="SEARCH_BUTTONS_LABEL_1"/></strong>
        </a>
    </div>

    <%--Reset--%>
    <div class="d-flex justify-content-end mb-2 ml-auto">
        <a href="${resetUrl}"
           class="btn btn-link btn-link-hover-primary-light btn-sm text-secondary text-truncate">
            <i class="glyphicons glyphicons-basic-reload"></i>
            <strong><op:translate key="SEARCH_BUTTONS_LABEL_2"/></strong>
        </a>
    </div>
</div>
