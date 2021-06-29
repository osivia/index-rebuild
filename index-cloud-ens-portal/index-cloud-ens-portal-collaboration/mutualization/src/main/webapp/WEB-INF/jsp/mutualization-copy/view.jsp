<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="submit" var="submitUrl"/>

<portlet:resourceURL id="browse" var="browseUrl">
    <portlet:param name="cmsBasePath" value="${form.basePath}"/>
    <portlet:param name="live" value="true"/>
    <portlet:param name="acceptedTypes" value="Folder"/>
</portlet:resourceURL>


<form:form action="${submitUrl}" method="post" modelAttribute="form">
    <%--Target path--%>
    <spring:bind path="targetPath">
        <div class="form-group required">
            <form:label path="targetPath"><op:translate key="MUTUALIZATION_COPY_TARGET_PATH_LABEL"/></form:label>
            <div class="card ${status.error ? 'border-danger is-invalid' : ''}">
                <div class="card-body">
                    <div class="selector">
                        <div class="fancytree fancytree-selector" data-lazyloadingurl="${browseUrl}"></div>
                        <form:hidden path="targetPath" cssClass="selector-value"/>
                    </div>
                </div>
            </div>
            <form:errors path="targetPath" cssClass="invalid-feedback"/>
        </div>
    </spring:bind>

    <%--Buttons--%>
    <div class="text-right">
            <%--Cancel--%>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">
            <span><op:translate key="CANCEL"/></span>
        </button>

            <%--Submit--%>
        <button type="submit" class="btn btn-primary ml-2">
            <span><op:translate key="MUTUALIZATION_COPY_SUBMIT"/></span>
        </button>
    </div>
</form:form>
