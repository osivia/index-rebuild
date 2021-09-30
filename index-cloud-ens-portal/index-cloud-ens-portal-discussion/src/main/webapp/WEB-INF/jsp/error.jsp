<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects />


<div class="alert alert-danger" role="alert">
    <div class="media">
        <div class="media-left">
            <i class="glyphicons glyphicons-remove-sign"></i>
        </div>
        
        <div class="media-body">
            <strong><op:translate key="ERROR" /></strong>
            <span>&ndash;</span>
            <span>${exception.message}</span>
        </div>
    </div>
</div>
