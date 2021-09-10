<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>


<%@ page isELIgnored="false" %>


<portlet:actionURL name="save" var="saveUrl"/>

<c:set var="namespace"><portlet:namespace/></c:set>


<form:form action="${saveUrl}" method="post" modelAttribute="windowProperties">
    <div class="form-group">
        <form:label path="view"><op:translate key="SEARCH_ADMIN_VIEW_LABEL"/></form:label>
        <c:forEach var="view" items="${views}">
            <div class="form-check">
                <form:radiobutton id="${namespace}-view-${view.id}" path="view" value="${view}" cssClass="form-check-input"/>
                <form:label for="${namespace}-view-${view.id}" path="view" cssClass="form-check-label"><op:translate key="${view.key}"/></form:label>
            </div>
        </c:forEach>
    </div>

    <%--Buttons--%>
    <div>
        <button type="submit" class="btn btn-primary"><op:translate key="SAVE"/></button>
        <button type="button" onclick="closeFancybox()" class="btn btn-secondary"><op:translate key="CANCEL"/></button>
    </div>
</form:form>
