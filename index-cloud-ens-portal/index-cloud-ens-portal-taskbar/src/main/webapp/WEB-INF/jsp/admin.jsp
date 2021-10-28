<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:defineObjects/>

<portlet:actionURL name="save" var="url"/>


<form:form action="${url}" method="post" modelAttribute="windowProperties" role="form">
    <%--Parent document path--%>
    <div class="form-group">
        <form:label path="path"><op:translate key="TASKBAR_ADMIN_PATH_LABEL"/></form:label>
        <form:input path="path" cssClass="form-control"/>
    </div>

    <%--Buttons--%>
    <div>
        <button type="submit" class="btn btn-primary"><op:translate key="SAVE"/></button>
        <button type="button" onclick="closeFancybox()" class="btn btn-secondary"><op:translate key="CANCEL"/></button>
    </div>
</form:form>
