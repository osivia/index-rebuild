<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<html>

<head>
    <%@include file="../includes/head.jspf" %>
</head>


<body class="fullheight overflow-hidden d-flex flex-column">

<%@include file="../includes/header.jspf" %>

<main class="d-flex flex-column flex-grow-1 overflow-auto bg-green-light background-clouds">
    <div class="container-fluid d-flex flex-column flex-grow-1 flex-shrink-0 py-4">
        <p:region regionName="top"/>

        <div class="row">
            <div class="col-md">
                <p:region regionName="col-1"/>
            </div>

            <div class="col-md">
                <p:region regionName="col-2"/>

                <%--Mes donn&eacute;es--%>
                <div class="card mb-3">
                    <div class="card-body">
                        <h3 class="h5 card-title">Mes donn&eacute;es</h3>
                        <p class="card-text">Par respect de la transparence vis-&agrave;-vis de la
                            protection des donn&eacute;es personnelles, vous pouvez consulter l'ensemble
                            de vos donn&eacute;es personnelles conserv&eacute;es sur nos serveurs.</p>
                        <a href="#" class="card-link">T&eacute;l&eacute;charger mes donn&eacute;es</a>
                    </div>
                </div>

                <%--Supprimer mon compte--%>
                <div class="card border-danger mb-3">
                    <div class="card-body text-danger">
                        <h3 class="h5 card-title">Suppression de mon compte</h3>
                        <p class="card-text">La suppression de votre compte effacera de mani&egrave;re d&eacute;finitive
                            l'ensemble de vos donn&eacute;es personnelles conserv&eacute;es sur nos
                            serveurs.</p>
                        <a href="#" class="btn btn-danger">Supprimer mon compte</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<%@include file="../includes/footer.jspf" %>

</body>

</html>
