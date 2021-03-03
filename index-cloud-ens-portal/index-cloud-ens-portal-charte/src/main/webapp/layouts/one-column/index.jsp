<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<html>

<head>
    <%@include file="../includes/head.jspf" %>
</head>


<body class="fullheight overflow-hidden d-flex flex-column">

<%@include file="../includes/header.jspf" %>

<main class="d-flex flex-column flex-grow-1 overflow-auto bg-green-light background-clouds">
    <div class="container d-flex flex-column flex-grow-1 flex-shrink-0 justify-content-center py-4">
        <p:region regionName="col-1"/>
    </div>
</main>

<%@include file="../includes/footer.jspf" %>

</body>

</html>
