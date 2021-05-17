<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:defineObjects/>

<portlet:actionURL name="save" var="url"/>


<c:set var="namespace"><portlet:namespace/></c:set>


<form:form action="${url}" method="post" modelAttribute="windowProperties">
    <%--NXQL request--%>
    <c:set var="placeholder"><op:translate key="FILE_BROWSER_ADMIN_FORM_NXQL_PLACEHOLDER"/></c:set>
    <div class="form-group">
        <form:label path="nxql">
            <span><op:translate key="FILE_BROWSER_ADMIN_FORM_NXQL_LABEL"/></span>

            <a href="#${namespace}-beanshell-example" data-toggle="collapse" class="ml-5 no-ajax-link">
                <span><op:translate key="FILE_BROWSER_ADMIN_FORM_BEANSHELL_EXAMPLE_TOGGLE"/></span>
            </a>
        </form:label>

        <div id="${namespace}-beanshell-example" class="collapse">
            <div class="card border-info mb-2">
                <div class="card-body text-info">
                    <%@ include file="../admin-beanshell-example.jspf" %>
                </div>
            </div>
        </div>

        <form:textarea path="nxql" cssClass="form-control" rows="10" placeholder="${placeholder}"/>
    </div>

    <%--BeanShell indicator--%>
    <div class="form-group">
        <div class="form-check">
            <form:checkbox path="beanShell" cssClass="form-check-input"/>
            <form:label path="beanShell"><op:translate key="FILE_BROWSER_ADMIN_FORM_BEANSHELL_LABEL"/></form:label>
        </div>
    </div>

    <%--Page size--%>
    <div class="form-group">
        <form:label path="pageSize"><op:translate key="FILE_BROWSER_ADMIN_FORM_PAGE_SIZE_LABEL"/></form:label>
        <form:input path="pageSize" cssClass="form-control"/>
    </div>

    <%--Buttons--%>
    <div>
        <%--Save--%>
        <button type="submit" class="btn btn-primary"><op:translate key="SAVE"/></button>

        <%--Cancel--%>
        <button type="button" onclick="closeFancybox()" class="btn btn-secondary"><op:translate key="CANCEL"/></button>
    </div>
</form:form>
