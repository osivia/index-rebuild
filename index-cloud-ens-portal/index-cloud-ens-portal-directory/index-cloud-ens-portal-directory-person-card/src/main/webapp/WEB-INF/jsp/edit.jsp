<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:actionURL name="save" var="saveUrl" copyCurrentRenderParameters="true"/>


<form:form action="${saveUrl}" method="post" modelAttribute="editionForm" enctype="multipart/form-data" role="form">
    <fieldset>
        <legend><op:translate key="PERSON_CARD_EDITION_LEGEND"/></legend>

            <%--Avatar--%>
        <%@ include file="edition/avatar.jspf" %>

            <%--Nickname--%>
        <%@ include file="edition/nickname.jspf" %>

            <%--First name--%>
        <%@ include file="edition/first-name.jspf" %>

            <%--Last name--%>
        <%@ include file="edition/last-name.jspf" %>

            <%--Mail--%>
        <%@ include file="edition/mail.jspf" %>

            <%--Buttons--%>
        <%@ include file="edition/buttons.jspf" %>
    </fieldset>
</form:form>
