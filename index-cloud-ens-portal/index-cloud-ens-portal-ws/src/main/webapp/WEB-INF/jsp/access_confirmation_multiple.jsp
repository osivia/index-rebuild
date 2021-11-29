<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<% session.setAttribute("displayAuthorize", System.currentTimeMillis()); %>




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

<body id="login" class="fullheight overflow-hidden d-flex flex-column uncluttered">

	<div id="container" class="d-flex flex-column flex-grow-1 overflow-auto">
		<header>
			<div id='simple-toolbar'>
				<div class=''>

					<nav class="navbar navbar-expand navbar-light">

						<a class="navbar-brand d-none d-md-inline-block mx-auto py-0" href="/portal"> <img alt="Cloud PRONOTE"
							src="/index-cloud-ens-charte/img/logo-cloud-pronote-large.png" height="32">
						</a>
					</nav>
				</div>
			</div>
		</header>
		<main id="content" class="d-flex flex-column flex-grow-1 overflow-auto">
		<div class="container my-auto py-4">
			<authz:authorize access="hasAnyRole('ROLE_USER')">
				<div class="box" id="login">
					<div class="row">
						<div class="col-lg-4 offset-lg-4">
							<form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize"
								method="post">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden"
									name="user_oauth_approval" value="true" />

								<div class="card bg-blue-lighter border-0 shadow-lg">
									<div class="card-body pb-0">
										<fieldset>
											<legend class="text-center text-muted">
												<span class="text-uppercase font-weight-bold">Autorisations</span>
											</legend>
											<p class="text-center">
		                                        <span>J'autorise l'application <b><c:out value="${clientName}"/></b> &agrave;
		                                        </span>
                                            </p>
										</fieldset>
									</div>


									<c:forEach items="${scopes}" var="scope" varStatus="status">
										<c:set var="approved">
											<c:if test="${scope.value}"> checked</c:if>
										</c:set>
										<c:set var="denied">
											<c:if test="${!scope.value}"> checked</c:if>
										</c:set>

										<div class="form-group">
											<div class="card mx-3">
												<div class="card-body text-center">
													<label> <c:choose>
															<c:when test="${scope.key eq 'scope.drive'}">
																<b>Acc&eacute;der &agrave; mes fichiers</b>
															</c:when>
															<c:otherwise>${scope.key}</c:otherwise>
														</c:choose>
													</label>
													<div>
														<div class="form-check form-check-inline">
															<input id="approved-${status.index}" type="radio" name="${scope.key}" value="true"
																class="form-check-input" ${scope.value ? 'checked' : ''}> <label for="approved-${status.index}"
																class="form-check-label">Approuver</label>
														</div>
														<div class="form-check form-check-inline">
															<input id="denied-${status.index}" type="radio" name="${scope.key}" value="false"
																class="form-check-input" ${scope.value ? '' : 'checked'}> <label for="denied-${status.index}"
																class="form-check-label">Refuser</label>
														</div>
													</div>
												</div>
											</div>
										</div>
									</c:forEach>

		                            <div class="col-md-auto align-self-end">
		                                 <div class="text-right m-2">
		                                    <button type="submit" name="submit" class="btn btn-secondary">
		                                        <span class="text-uppercase font-weight-bold">Valider</span>
		                                    </button>
		                                </div>
		                            </div>

								</div>

							</form>
						</div>
					</div>
				</div>

			</authz:authorize>
		</div>
		</main>
	</div>

</body>

</html>
