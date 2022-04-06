<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<!DOCTYPE html>
<html lang="fr">

<head>
<meta charset="UTF-8">
<meta name="application-name" content="Cloud Index Education">
<meta http-equiv="cache-control" content="no-cache, no-store, must-revalidate">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="expires" content="0">

<meta name="application-name" content="Cloud Index Education">


<op:resourceAdapter directive="<script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery/jquery-1.12.4.min.js'></script>" />
<op:resourceAdapter directive="<script type='text/javascript' src='/osivia-portal-custom-web-assets/js/jquery-integration.min.js'></script>" />
<op:resourceAdapter directive="<script type='text/javascript' src='/osivia-portal-custom-web-assets/components/jquery-ui/jquery-ui-1.11.3.min.js'></script>" />
<op:resourceAdapter directive="<link rel='stylesheet' href='/osivia-portal-custom-web-assets/components/jquery-ui/jquery-ui-1.11.3.min.css'>" />
<op:resourceAdapter directive="<script type='text/javascript'   src='/osivia-portal-custom-web-assets/components/jquery-mobile/jquery.mobile.custom.min.js'></script>" />
<op:resourceAdapter directive="<link rel='stylesheet' href='/index-cloud-ens-charte/css/cloud-ens.css' />" />
<op:resourceAdapter directive="<script src='/osivia-portal-custom-web-assets/components/bootstrap/js/bootstrap.bundle.min.js'></script>" />


</head>


<body id="login" class="fullheight overflow-hidden d-flex flex-column">

<header>
    <div id='simple-toolbar'>
            <nav class="navbar navbar-expand-md navbar-dark bg-dark">
                <%--Brand--%>
                <a href="/portal" class="navbar-brand mx-auto py-0">
                    <img alt="Cloud PRONOTE"
                         src="/index-cloud-ens-charte/img/logo-cloud-pronote-toolbar.png" height="45">
                </a>
            </nav>                    
    </div>
    
</header>
        
<main class="d-flex flex-column flex-grow-1 overflow-auto bg-green-light background-clouds">
    <div class="container d-flex flex-column flex-grow-1 flex-shrink-0 justify-content-center py-4">





			<div class="box" id="login">
				<div class="row">
					<div class="col-lg-4 offset-lg-4">
						<form action="<c:url value="/login"/>" method="post" role="form">

                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            
							<div class="card bg-blue-lighter border-0 shadow-lg">
								<div class="card-body">
									<fieldset>
										<legend class="text-center">
											<span class="text-uppercase font-weight-bold">Authentification</span>
										</legend>
										<p class="text-center text-muted">
											<span>Merci de renseigner votre identifiant et votre mot de passe du Cloud PRONOTE.
											</span>
										</p>

										<c:if test="${not empty param.authentication_error}">
									        <div id="msg" class="invalid-feedback d-block my-3 text-center">Mauvais identifiant / mot de passe.</div>
									    </c:if>
									    <c:if test="${not empty param.authorization_error}">
									        <div id="msg" class="invalid-feedback d-block my-3 text-center">Vous n'avez pas les droits pour acc&eacute;der &agrave; cette ressource.</div>
									    </c:if>


										<div>

											<div class="form-group">
                                                <label for="username" class="sr-only"><span class="accesskey">A</span>dresse mail:</label>
                                                <input id="username" type="text" name="username" class="form-control" placeholder="adresse mail"/>
											</div>

								            <div class="form-group">
								                <label for="password" class="sr-only"><span class="accesskey">M</span>ot de passe:</label>
								                <input id="password" type="password" name="password" placeholder="mot de passe" class="form-control" autocomplete="off"/>
								                <span id="capslock-on" class="form-text" style="display: none;">la touche Verr Maj est activée !</span>
								            </div>

										</div>

									</fieldset>
								</div>

                                <div class="col-md-auto align-self-end">
                                    <div class="text-right m-2">
                                        <button type="submit" name="submit" class="btn btn-primary">
                                            <span class="text-uppercase font-weight-bold">Valider</span>
                                        </button>
                                    </div>
                                </div>

							</div>

						</form>
					</div>
				</div>
			</div>

		<!-- END #content -->

	</div>
	<!-- END #container -->

</main>

</body>

</html>
