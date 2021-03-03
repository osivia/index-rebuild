<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<html>

<head>
    <%@include file="../includes/head.jspf" %>
</head>


<body class="fullheight overflow-hidden d-flex flex-column" data-drawer="true">

<%@include file="../includes/header.jspf" %>

<main class="d-flex flex-column flex-grow-1 overflow-auto">
    <div class="container-fluid d-flex flex-column flex-grow-1">
        <div class="row flex-grow-1 flex-nowrap">
            <%@include file="../includes/nav.jspf" %>

            <div class="col d-md-flex flex-column overflow-auto py-4 px-md-5 pt-md-5 pb-md-3 bg-green-light background-clouds">
                <%--Breadcrumb--%>
                <p:region regionName="breadcrumb"/>

                <p:region regionName="maximized"/>
            </div>
        </div>
    </div>
</main>

<%@include file="../includes/footer.jspf" %>

</body>

</html>
