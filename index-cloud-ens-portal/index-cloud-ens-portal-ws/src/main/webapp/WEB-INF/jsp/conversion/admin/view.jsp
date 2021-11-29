<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="save" var="saveUrl" copyCurrentRenderParameters="true" />


<div class="edit-portal-group portlet-filler d-flex flex-column flex-grow-1">

	<form:form action="${saveUrl}" method="post" modelAttribute="conversionForm" cssClass="form-horizontal d-flex flex-column flex-grow-1"
		enctype="multipart/form-data" role="form">

		<div class="row  flex-grow-1">
			<div class="col-lg-6 d-flex flex-column">
			     <div class="d-flex flex-column flex-grow-1">
			        <div class="flex-grow-1">
					<fieldset>
						<legend>
							<op:translate key="CONVERSION_ADMIN_FILE_LEGEND" />
						</legend>
						<!-- file conversion -->
						<%@ include file="view/file.jspf"%>
	
	
					</fieldset>
				</div>



				<c:if test="${not empty conversionForm.fileDownloadUrl}">
                                               <div class="flex-grow-1 mt-3">					


						<fieldset>
							<legend>
								<op:translate key="CONVERSION_ADMIN_PATCH" />
							</legend>

							<!-- patch- -->
							<%@ include file="view/patch.jspf"%>

						</fieldset>
					</div>
				</c:if>
			     </div>
			</div>

			<div class="col-lg-6 d-flex flex-column">
				<fieldset>
					<legend>
						<op:translate key="CONVERSION_ADMIN_LOGS_LEGEND" />
					</legend>

					<!-- logs- -->
					<%@ include file="view/logs.jspf"%>
				</fieldset>
			</div>
		</div>

	</form:form>
</div>
