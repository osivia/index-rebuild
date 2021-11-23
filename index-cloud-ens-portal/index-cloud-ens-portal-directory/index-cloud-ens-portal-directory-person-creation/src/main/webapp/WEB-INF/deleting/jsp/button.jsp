<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>

<%@ page isELIgnored="false" %>


<portlet:actionURL name="view-form" var="url"/>

<div class="d-flex flex-row-reverse">


	<a href="${url}" class="btn btn-primary">
	    <span><op:translate key="deleting.title"/></span>
	</a>

</div>