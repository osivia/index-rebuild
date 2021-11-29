<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>



<%@ page contentType="text/html" isELIgnored="false" %>


<portlet:defineObjects />


<div class="ws-dashboard">


	
<c:choose>	
	<c:when test="${empty dashboard.applications}">
	    <p class="text-muted"><op:translate key="DASHBOARD_EMPTY_MESSAGE"/></p>
    </c:when>
	
	<c:otherwise>

	   <div class="d-flex">	
       	    <span><op:translate key="DASHBOARD_NOTEMPTY_MESSAGE"/></span>
       	</div>

		<c:forEach var="application" items="${dashboard.applications}">
			<portlet:actionURL name="revoke" var="revokeUrl">
    			<portlet:param name="clientId" value="${application.clientId}" />
			</portlet:actionURL>


		    <div class="form-check ml-2">
		
		        <input type="checkbox" checked class="form-check-input" onClick="openDashboardModal('${namespace}-revoke-modal', '${revokeUrl}');">
		        <label >${application.clientName}</label>
		                     
		    </div>

        </c:forEach>
    </c:otherwise>
</c:choose>
	        
</div>



    <!-- Restore all modal -->
    <div id="${namespace}-revoke-modal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <span><op:translate key="DASHBOARD_REVOKE_MODAL_TITLE"/></span>
                    </h5>

                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="modal-body">
                    <p>
                        <span><op:translate key="DASHBOARD_REVOKE_MODAL_MESSAGE"/></span>
                    </p>
                </div>

                <div class="modal-footer">
                    <a href="#" class="btn btn-primary revoke-target" data-dismiss="modal">
                        <span><op:translate key="DASHBOARD_REVOKE_CONFIRM"/></span>
                    </a>

                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                        <span><op:translate key="CANCEL"/></span>
                    </button>
                </div>
            </div>
        </div>
    </div>