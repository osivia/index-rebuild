<!DOCTYPE html>
<%@ taglib prefix="p" uri="portal-layout"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<html>

<head>
<%@include file="../includes/head.jspf"%>
</head>


<body class="fullheight overflow-hidden d-flex flex-column">

	<%@include file="../includes/header.jspf"%>



	<main class="d-flex flex-column flex-grow-1 overflow-auto bg-green-light background-clouds">
	<div class="container d-flex flex-column flex-sm-row flex-grow-1 flex-shrink-0 justify-content-center py-4">

		<div class="my-auto mx-auto mx-sm-0">
			<img alt="Cloud PRONOTE" src="/index-cloud-ens-charte/img/logo-cloud-pronote.svg" height="200px">
		</div>
		
		<div class="ml-5 my-auto">
			<p class="mb-3">
				<strong>Ce Cloud est destin&eacute; &agrave; l'ensemble des enseignants</strong>
			</p>

			<p class="mb-2 text-green-dark"><small><strong>Il permet &agrave; chaque enseignant&nbsp;:</strong></small></p>

			<ul class="pl-5 text-green-dark ">
				<li class="mb-1"><small><strong>de stocker ses documents &agrave; usage p&eacute;dagogique</strong></small></li>
				<li class="mb-1"><small><strong>d'y acc&eacute;der &agrave; partir de tout poste de travail, tablette et
						smartphone chez lui ou dans son &eacute;tablissement</strong></small></li>
				<li class="mb-1"><small><strong>de publier ses documents directement depuis PRONOTE</strong></small></li>
				<li class="mb-1"><small><strong>d'acc&eacute;der &agrave; un r&eacute;f&eacute;rentiel mutualis&eacute; de
						ressources p&eacute;dagogiques qu'il pourra &eacute;galement alimenter</strong></small></li>
			</ul>

			<p class="text-green-dark mb-0"><small><strong>Vous pouvez cr&eacute;er votre compte Cloud depuis PRONOTE.</strong></small></p>

		</div>

	</div>
	</main>

	<%@include file="../includes/footer.jspf"%>

</body>

</html>
