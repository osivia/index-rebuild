<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>



<c:set var="brand"><op:translate key="BRAND"/></c:set>
<c:choose>
	<c:when test="${not empty param['httpCode']}">
        <op:status code="${param['httpCode']}"  /> 
	</c:when>
</c:choose>


<html>

<head>
    <title><op:translate key="ERROR"/> - ${brand}</title>

    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <meta http-equiv="default-style" content="Cloud-ens">

    <link rel="icon" type="image/png" href="${contextPath}/img/favicon.ico"/>
    <link rel="stylesheet" href="${contextPath}/css/cloud-ens.css" title="Cloud-ens"/>
</head>


<body class="fullheight overflow-hidden d-flex flex-column">

<header>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark">
        <%--Brand--%>
        <a href="/" class="navbar-brand mx-auto py-0">
            <img alt="${requestScope['osivia.header.application.name']}"
                 src="${contextPath}/img/logo-cloud-pronote-toolbar.png" height="45">
        </a>
    </nav>
</header>

<main class="d-flex flex-column flex-grow-1 overflow-auto bg-green-light background-clouds">
    <div class="container d-flex flex-column flex-grow-1 flex-shrink-0 justify-content-center py-4">
        <div class="row justify-content-center flex-grow-1">
            <div class="col-auto my-auto">
                <div class="card border-danger my-5 text-center">
                    <div class="card-body">
                        <h1 class="h4 card-title text-danger">
                            <span><op:translate key="ERROR"/></span>
                        </h1>
                        <h2 class="h5 card-subtitle mb-3 text-danger">
                            <c:choose>
                                <c:when test="${(param['httpCode'] eq 401) || (param['httpCode'] eq 403) || (param['httpCode'] eq 404) || (param['httpCode'] eq 500)}">
                                    <span>${param['httpCode']}</span>
                                    <small><op:translate key="ERROR_${param['httpCode']}"/></small>
                                </c:when>

                                <c:otherwise>
                                    <small><op:translate key="ERROR_GENERIC_MESSAGE"/></small>
                                </c:otherwise>
                            </c:choose>
                        </h2>

                        <c:if test="${(param['httpCode'] eq 401) || (param['httpCode'] eq 403) || (param['httpCode'] eq 404) || (param['httpCode'] eq 500)}">
                            <p class="card-text">
                                <span><op:translate key="ERROR_${param['httpCode']}_MESSAGE"/></span>
                            </p>
                        </c:if>

                        <a href="/" class="card-link">
                            <span><op:translate key="BACK_TO_HOME"/></span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
     </div>
</main>

</body>

</html>
