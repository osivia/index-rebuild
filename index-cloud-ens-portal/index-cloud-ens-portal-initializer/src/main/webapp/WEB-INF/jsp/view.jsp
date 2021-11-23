<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal"
	prefix="op"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<portlet:defineObjects />
<portlet:actionURL var="checkInitialized">
	<portlet:param name="action" value="checkInit" />
</portlet:actionURL>

<div class="initialization">

	<form:form action="${checkInitialized}" method="post"
		cssClass="form-horizontal">

		<c:set var="title">
			<op:translate key="INIT_TITLE" />
		</c:set>
		<button type="submit" class="btn btn-primary">

			<span>${title}</span>
		</button>

	</form:form>
</div>