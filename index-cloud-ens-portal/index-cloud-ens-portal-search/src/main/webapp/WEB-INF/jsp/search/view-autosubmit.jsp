<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="search" var="searchUrl"/>

<c:set var="namespace"><portlet:namespace/></c:set>


<div class="search auto-submit">
    <form:form id="${namespace}-form" action="${searchUrl}" method="post" modelAttribute="form">
        <c:set var="placeholder"><op:translate key="SEARCH_FILTERS_KEYWORDS_PLACEHOLDER"/></c:set>
        <div class="form-group mb-0">
            <form:label path="value" cssClass="sr-only"><op:translate key="SEARCH_INPUT_VALUE_LABEL"/></form:label>
            <form:input path="value" type="search" placeholder="${placeholder}" cssClass="form-control form-control-sm"></form:input>
            <button type="submit" class="d-none"></button>
        </div>
    </form:form>
</div>
