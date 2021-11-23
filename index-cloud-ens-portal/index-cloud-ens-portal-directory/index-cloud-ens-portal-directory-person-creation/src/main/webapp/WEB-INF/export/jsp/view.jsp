<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%@ page contentType="text/html" isELIgnored="false" %>

<portlet:actionURL name="exportData" var="exportDataUrl" copyCurrentRenderParameters="true"/>


<p><op:translate key="exportaccount.info" /></p>

<div class="export-account">
	<form:form action="${exportDataUrl}" method="post" modelAttribute="form"  enctype="multipart/form-data" role="form" >
	
		<c:if test="${empty form.exports}">
			<p class="text-muted"><op:translate key="exportaccount.empty"/></p>
		</c:if>
	
		<ul>
			<c:forEach items="${form.exports}" var="export">
				<li><fmt:formatDate value="${export.value.date}" type="both" dateStyle="short" timeStyle="short"/>
				
					<c:choose>
						<c:when test="${export.value.status eq 'RUNNING'}">
							<p><op:translate key="exportaccount.running"/></p>
						</c:when>
						<c:when test="${export.value.status eq 'DONE'}">
							<portlet:resourceURL id="download" var="downloadUrl">
								<portlet:param name="file" value="${export.value.zipFilename}"/>
							</portlet:resourceURL>
							<portlet:actionURL name="remove" var="removeUrl">
								<portlet:param name="uuid" value="${export.key}"/>
							</portlet:actionURL>

							<p>
								<a class="no-ajax-link" href="${downloadUrl}"><op:translate key="exportaccount.action.download"/></a>
								<a href="${removeUrl}" class="text-danger"><op:translate key="exportaccount.action.delete"/></a>
							</p>
						</c:when>
					</c:choose>
				</li>
				
			</c:forEach>
		</ul>
	
		<c:if test="${form.limitReached or form.isExportRunning}" >
			<c:set value="disabled=disabled" var="exportDisabled"></c:set>
		</c:if>
	
	        <hr>
  	
		
		<c:if test="${form.limitReached}">
		      <div class="alert alert-info">
		              <op:translate key="exportaccount.error.limitReached"/>
		      </div>
		</c:if>
		<c:if test="${form.isExportRunning}">
		      <div class="alert alert-info">
		              <op:translate key="exportaccount.error.isExportRunning"/>
		      </div>
		</c:if>
		
		<div class="text-right">
                    <div class="col-md-9 offset-md-3 col-lg-10 offset-lg-2">
                        <!-- Save -->
                        <button type="submit" name="export" class="btn btn-primary" ${exportDisabled}>
                            <span><op:translate key="exportaccount.submit" /></span>
                        </button>
                    </div>
                </div>
	
	</form:form>
</div>