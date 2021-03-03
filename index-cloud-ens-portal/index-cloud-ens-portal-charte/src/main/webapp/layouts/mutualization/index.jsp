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
        <div class="row flex-grow-1 flex-nowrap overflow-auto">
            <op:resizable cssClass="col-auto d-md-flex flex-column" minWidth="290">
                <div class="row flex-grow-1 flex-nowrap">
                    <div id="drawer" class="col d-flex flex-column overflow-auto bg-light shadow">
                        <div class="py-4 py-md-5">
                            <p>
                                <strong><op:translate key="LAYOUT_MUTUALIZATION_NAV_1_TITLE"/></strong>
                            </p>

                            <p:region regionName="nav-1"/>

                            <hr class="my-4">

                            <p>
                                <strong><op:translate key="LAYOUT_MUTUALIZATION_NAV_2_TITLE"/></strong>
                            </p>

                            <div class="ml-3">
                                <p:region regionName="nav-2"/>
                            </div>
                        </div>
                    </div>
                </div>
            </op:resizable>

            <div class="col-md d-md-flex flex-column overflow-auto py-4 px-md-5 pt-md-5 pb-md-3 bg-orange-light background-clouds">
                <p:region regionName="top"/>
            </div>
        </div>
    </div>
</main>

<%@include file="../includes/footer.jspf" %>

</body>

</html>
