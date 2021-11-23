<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:actionURL name="sendMail" var="sendMailUrl" copyCurrentRenderParameters="true"/>


<div class="row">
	<div class="col-md-6 offset-md-3 col-lg-4 offset-lg-4">
		<div class="card bg-blue-lighter shadow-lg">
			<div class="card-body">
				<h3 class="card-title h5"><op:translate key="renew.init.title"/></h3>

				<form:form action="${sendMailUrl}" method="post" modelAttribute="form" enctype="multipart/form-data" role="form">
					<spring:bind path="mail">
						<div class="form-group required">
							<form:label path="mail"><op:translate key="renew.init.mail" /></form:label>
							<c:set var="placeholder"><op:translate key="renew.init.mail.placeholder"/></c:set>
							<form:input path="mail" type="email" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="${placeholder}" />
							<form:errors path="mail" cssClass="invalid-feedback" />
						</div>
					</spring:bind>

					<div class="text-right">
						<button type="submit" name="save" class="btn btn-primary">
							<span><op:translate key="renew.init.submit" /></span>
						</button>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>




