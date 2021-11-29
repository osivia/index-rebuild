<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>



<!DOCTYPE html>
<html lang="en">

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
											
		                                    <c:forEach items="${scopes}" var="scope" varStatus="status">
		                                        <c:set var="approved">
		                                            <c:if test="${scope.value}"> checked</c:if>
		                                        </c:set>
		                                        <c:set var="denied">
		                                            <c:if test="${!scope.value}"> checked</c:if>
		                                        </c:set>
		
		                                        <div class="form-group p-2">
		                                                    <input id="approved-${status.index}" type="checkbox" name="${scope.key}" value="true"
		                                                                class="form-check-input" ${scope.value ? 'checked' : ''}> 
		                                                
		                                                    <label> <c:choose>
		                                                            <c:when test="${scope.key eq 'scope.drive'}">
		                                                                Je souhaite acc&eacute;der &agrave; mon Cloud PRONOTE et aux fichiers qu'il contient depuis le PRONOTE de l'&eacute;tablissement <b><c:out value="${clientName}"/></b> .
		                                                            </c:when>
		                                                            <c:otherwise>J'autorise l'application <b><c:out value="${clientName}"/></b> &agrave; ${scope.key}</c:otherwise>
		                                                        </c:choose>
		                                                    </label>
		                                        </div>
		                                    </c:forEach>											
											
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

			</authz:authorize>
    </div>
    <!-- END #container -->

</main>

</body>

</html>
