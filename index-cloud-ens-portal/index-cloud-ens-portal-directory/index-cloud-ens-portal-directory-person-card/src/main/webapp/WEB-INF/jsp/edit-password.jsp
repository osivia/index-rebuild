<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:actionURL name="save" var="saveUrl" copyCurrentRenderParameters="true" />

<portlet:resourceURL id="password-information" var="passwordInformationUrl"/>


<div class="person-card-password-edition">
    <form:form action="${saveUrl}" method="post" modelAttribute="passwordEditionForm" enctype="multipart/form-data" role="form">
        <fieldset>
            <legend><op:translate key="PERSON_CARD_PASSWORD_EDITION_LEGEND"/></legend>

            <c:if test="${not passwordEditionForm.overwrite}">
                <!-- Current password -->
                <spring:bind path="current">
                    <div class="form-group row required">
                        <form:label path="current" cssClass="col-sm-5 col-md-4 col-lg-3 col-form-label"><op:translate key="PERSON_CARD_PASSWORD_CURRENT" /></form:label>
                        <div class="col">
                            <form:password path="current" cssClass="form-control ${status.error ? 'is-invalid' : ''}" />
                            <form:errors path="current" cssClass="invalid-feedback" />
                        </div>
                    </div>
                </spring:bind>
            </c:if>

            <!-- Update password value -->
            <spring:bind path="update">
                <div class="form-group row required password-control">
                    <form:label path="update" cssClass="col-sm-5 col-md-4 col-lg-3 col-form-label"><op:translate key="PERSON_CARD_PASSWORD_UPDATE" /></form:label>
                    <div class="col">
                        <form:password path="update" cssClass="form-control ${status.error ? 'is-invalid' : ''}"  data-password-control-url="${passwordInformationUrl}"/>
                        <form:errors path="update" cssClass="invalid-feedback" />
                    </div>
                </div>
            </spring:bind>

            <!-- Update password confirmation -->
            <spring:bind path="confirmation">
                <div class="form-group row required">
                    <form:label path="confirmation" cssClass="col-sm-5 col-md-4 col-lg-3 col-form-label"><op:translate key="PERSON_CARD_PASSWORD_CONFIRMATION" /></form:label>
                    <div class="col">
                        <form:password path="confirmation" cssClass="form-control ${status.error ? 'is-invalid' : ''}" />
                        <form:errors path="confirmation" cssClass="invalid-feedback" />
                    </div>
                </div>
            </spring:bind>

            <!-- Buttons -->
            <%@ include file="edition/buttons.jspf" %>
        </fieldset>
    </form:form>
</div>