<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<p class="text-danger mb-0">
    <i class="glyphicons glyphicons-basic-circle-alert"></i>
    <span><op:translate key="ERROR" /></span>

     <c:if test="${not empty param['token'] }">
      <!-- TOKEN :  ${param['token']} -->
  	</c:if>
    
</p>
