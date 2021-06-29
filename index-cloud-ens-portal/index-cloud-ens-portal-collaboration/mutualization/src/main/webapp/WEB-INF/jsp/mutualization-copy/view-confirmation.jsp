<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<c:set var="namespace"><portlet:namespace/></c:set>

<portlet:renderURL var="backUrl"/>
<portlet:actionURL name="submit" var="submitUrl" copyCurrentRenderParameters="true"/>


<form:form action="${submitUrl}" method="post" modelAttribute="confirmationForm">
    <%--Location--%>
    <c:if test="${not empty confirmationForm.location}">
        <div class="form-group">
            <label><op:translate key="MUTUALIZATION_COPY_TARGET_PATH_LABEL"/></label>
            <div><ttc:title document="${confirmationForm.location}" icon="true" linkable="false"/></div>
        </div>
    </c:if>


    <%--Alert--%>
    <div class="alert alert-warning" role="alert">
        <span><op:translate key="MUTUALIZATION_COPY_CONFIRMATION_LEGEND"/></span>
    </div>


    <%--Choice--%>
    <div class="form-group">
        <form:label path="choice"><op:translate key="MUTUALIZATION_COPY_CONFIRMATION_CHOICE_LABEL"/></form:label>
        <c:forEach var="choice" items="${choices}" varStatus="status">
            <div class="form-check">
                <form:radiobutton id="${namespace}-choice-${status.index}" path="choice" value="${choice.key}"
                                  cssClass="form-check-input" cssErrorClass="form-check-input is-invalid"/>
                <label for="${namespace}-choice-${status.index}" class="form-check-label">${choice.value}</label>
                <c:if test="${status.last}">
                    <form:errors path="choice" cssClass="invalid-feedback"/>
                </c:if>
            </div>
        </c:forEach>
    </div>


    <%--Back--%>
    <div class="form-group">
        <a href="${backUrl}"><op:translate key="MUTUALIZATION_COPY_CONFIRMATION_BACK"/></a>
    </div>


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
