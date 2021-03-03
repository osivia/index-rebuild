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
                <p:region regionName="top"/>

                <div class="row">
                    <div class="col-md">
                        <p:region regionName="col-1"/>
                    </div>

                    <div class="col-md">
                        <p:region regionName="col-2"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@include file="../includes/footer.jspf" %>

</body>

</html>
