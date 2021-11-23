<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page contentType="text/html" isELIgnored="false" %>

<portlet:defineObjects/>


<portlet:actionURL name="submitPassword" var="submitPasswordUrl"/>
<portlet:resourceURL id="password-information" var="passwordInformationUrl"/>


<div class="row">
	<div class="col-md-6 offset-md-3 col-lg-4 offset-lg-4">
		<div class="card bg-blue-lighter shadow-lg renew-password">
			<div class="card-body">
				<h3 class="card-title h5"><op:translate key="renew.password.title"/></h3>

				<form:form action="${submitPasswordUrl}" method="post" modelAttribute="form" role="form">
					<div class="form-group">
						<form:label path="mail"><op:translate key="renew.password.mail" /></form:label>
						<form:input path="mail" disabled="true" cssClass="form-control" />
					</div>

					<spring:bind path="newpassword">
						<div class="form-group required password-control">
							<form:label path="newpassword"><op:translate key="renew.password.password" /></form:label>
							<form:password path="newpassword" cssClass="form-control" cssErrorClass="form-control is-invalid"  data-password-control-url="${passwordInformationUrl}"/>
							<form:errors path="newpassword" cssClass="invalid-feedback" />
							
						</div>
					</spring:bind>

					<spring:bind path="password2">
						<div class="form-group required">
							<form:label path="password2"><op:translate key="renew.password.password2" /></form:label>
							<form:password path="password2" cssClass="form-control" cssErrorClass="form-control is-invalid" />
							<form:errors path="password2" cssClass="invalid-feedback" />
						</div>
					</spring:bind>

					<div class="text-right">
						<button type="submit" name="save" class="btn btn-primary">
							<span><op:translate key="renew.password.submit" /></span>
						</button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
