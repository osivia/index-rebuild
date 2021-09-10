<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="save" var="url"/>


<%--@elvariable id="windowProperties" type="fr.index.cloud.ens.search.saved.portlet.model.SavedSearchesWindowProperties"--%>
<form:form action="${url}" method="post" modelAttribute="windowProperties">
    <%--Saved searches category identifier--%>
    <div class="form-group">
        <form:label path="categoryId"><op:translate key="SAVED_SEARCHES_FORM_CATEGORY_ID_LABEL"/></form:label>
        <form:input path="categoryId" cssClass="form-control"/>
        <small class="form-text text-muted"><op:translate key="SAVED_SEARCHES_FORM_CATEGORY_ID_HELP"/></small>
    </div>

    <%--Buttons--%>
    <div>
        <button type="submit" class="btn btn-primary"><op:translate key="SAVE"/></button>
        <button type="button" onclick="closeFancybox()" class="btn btn-secondary"><op:translate key="CANCEL"/></button>
    </div>
</form:form>
