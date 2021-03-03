<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>


<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <%--Brand--%>
    <a href="${requestScope['osivia.home.url']}" class="navbar-brand mx-auto py-0">
        <img alt="${requestScope['osivia.header.application.name']}"
             src="${contextPath}/img/logo-cloud-pronote-toolbar.png" height="45">
    </a>
</nav>
