<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice" prefix="ttc"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:actionURL name="deleteDiscussion" var="deleteDiscussionUrl" copyCurrentRenderParameters="true" />


<c:set var="deleteDiscussionConfirmationModalId" value="${namespace}-delete-confirmation" />

<c:if test="${not empty detailForm.document.publication.targetDTO}">
    <c:set var="publication" value="${detailForm.document.publication.targetDTO}" />

	<div class="d-flex justify-content-between flex-wrap">
	    <div class="d-flex mt-1 mb-2">
	        <%--Icon--%>
	        <div class="flex-shrink-0 mr-2">
	            <ttc:icon document="${publication}"/>
	        </div>
	
	        <%--Title--%>
	        <div class="flex-shrink-1 mr-2 text-truncate">
	            <strong>${publication.displayTitle}</strong>
	        </div>
	
	        <%--Size--%>
	        <c:if test="${publication.type.file}">
	            <c:set var="size" value="${publication.properties['file:content']['length']}"/>
	            <div class="flex-shrink-0 mr-2">
	                <small class="text-secondary"><ttc:fileSize size="${size}"/></small>
	            </div>
	        </c:if>
	
	        <%--Mutualized indicator--%>
	        <c:if test="${publication.properties['mtz:enable']}">
	            <div class="flex-shrink-0 mr-2">
	                <span class="text-orange">
	                    <i class="glyphicons glyphicons-basic-share"></i>
	                </span>
	            </div>
	        </c:if>
	    </div>
	</div>    
</c:if>

<div class="portlet-filler d-flex flex-column pr-1 flex-grow-1 discussion  col ">

	<c:set var="deleteMessageConfirmationModalId" value="${namespace}-delete-confirmation" />
	<c:set var="reportMessageConfirmationModalId" value="${namespace}-report-confirmation" />




	<portlet:actionURL name="deleteMessage" var="deleteMessageUrl" copyCurrentRenderParameters="true" />
	<portlet:actionURL name="reportMessage" var="reportMessageUrl" copyCurrentRenderParameters="true" />
	<portlet:actionURL name="addMessage" var="addMessageUrl" copyCurrentRenderParameters="true" />




	<!-- anchor : ${detailForm.anchor} -->

	<c:if test="${not empty detailForm.anchor}">
		<script type="text/javascript">
			setTimeout(function() {
				document.getElementById("${detailForm.anchor}")
						.scrollIntoView();
			}, 100);
		</script>
	</c:if>



	<div class="row flex-nowrap">
		<div class="col overflow-auto pt-4 py-md-2 mb-md-2">

			<form:form action="${addMessageUrl}" method="post" modelAttribute="detailForm" cssClass="form-horizontal">

				<c:forEach var="message" items="${detailForm.document.messages}">

					<c:if test="${not message.deleted}">

						<div class="row" id="msg-${message.id}">
							<c:if test="${detailForm.author eq message.author}">


								<div class="offset-2  col-10  mt-2">
									<div class="text-right text-muted small">
										<c:choose>
											<c:when test="${message.date gt detailForm.today}">
												<fmt:formatDate value="${message.date}" type="time" timeStyle="short" />
											</c:when>
											<c:otherwise>
												<fmt:formatDate value="${message.date}" type="both" dateStyle="medium" timeStyle="short" />
											</c:otherwise>
										</c:choose>
									</div>
									<div class="border rounded mb-2 p-3 bg-light-green-block "><span>${message.content}</span></div>
								</div>
							</c:if>

							<c:if test="${detailForm.author ne message.author}">

								<c:set var="messageBackgroundColor">${ detailForm.options.mode eq  'admin' && message.id eq detailForm.options.messageId ? "bg-warning" : "bg-light" }</c:set>

								<div class="col-10  mt-2">
									<div class="d-flex flex-row-reverse">
										<div class="text-muted small">


											<c:choose>
												<c:when test="${message.date gt detailForm.today}">
													<fmt:formatDate value="${message.date}" type="time" timeStyle="short" />
												</c:when>
												<c:otherwise>
													<fmt:formatDate value="${message.date}" type="both" dateStyle="medium" timeStyle="short" />
												</c:otherwise>
											</c:choose>


											<span> <c:if test="${ not (detailForm.options.mode eq  'admin') }">
													<%--Report--%>
													<a href="javascript:" style="margin-top: -6px"
														class="btn btn-sm ml-1 no-ajax-link glyphicons glyphicons-warning-sign text-muted float-right"
														data-toggle="modal" data-target="#${reportMessageConfirmationModalId}" data-message-id="${message.id}">
													</a>
												</c:if> <c:if test="${ detailForm.options.mode eq  'admin'}">
													<%--Delete--%>
													<a href="javascript:" style="margin-top: -6px"
														class="btn btn-sm ml-1 no-ajax-link glyphicons glyphicons-basic-bin text-muted float-right"
														data-toggle="modal" data-target="#${deleteMessageConfirmationModalId}" data-message-id="${message.id}">

													</a>
												</c:if>
											</span>

										</div>
									</div>

									<div class="border rounded mb-2 p-3 ${messageBackgroundColor}">
									    <div class="font-weight-bold my-1  text-green-dark">
                                            <ttc:user name="${message.author}" linkable="false" />
                                        </div>
									
									   <p>${message.content}</p>
                                    </div>
								</div>
							</c:if>
						</div>
					</c:if>

				</c:forEach>
				<div id="last-message"></div>
			</form:form>
		</div>
	</div>



	<c:if test="${ not (detailForm.options.mode eq  'admin') }">
		<div class="row flex-shrink-0">
			<form:form action="${addMessageUrl}" method="post" modelAttribute="detailForm"
				cssClass="form-horizontal  d-flex flex-grow-1">
				<div class="offset-2  col-10 text-right">
					<div class="mt-2 d-flex">
						<c:set var="placeholder">
							<op:translate key="DISCUSSION_NEW_MESSAGE_PLACEHOLDER" />
						</c:set>
						<form:textarea path="newMessage" rows="1" cssClass="form-control form-rounded" placeholder="${placeholder}" />
						<button type="submit" class="btn btn-link btn-link-hover-primary btn-sm my-auto ml-2 text-primary-dark ">
                        	<i class="glyphicons glyphicons-basic-send"></i>
                        	<span class="sr-only">${title}</span>
                    	</button>						
					</div>
				</div>
			</form:form>
		</div>
	</c:if>








	<%--Delete modal confirmation--%>
	<div id="${deleteMessageConfirmationModalId}" class="modal fade" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<op:translate key="DISCUSSION_DELETE_MESSAGE_CONFIRMATION_MODAL_BODY" />
					</h5>
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
				</div>

				<div class="modal-body">
					<p>
						<op:translate key="DISCUSSION_DELETE_MESSAGE_CONFIRMATION_MODAL_DETAIL" />
					</p>
					<form action="${deleteMessageUrl}" method="post">
						<input type="hidden" name="messageIndice"> <input type="submit" class="d-none">
					</form>
				</div>

				<div class="modal-footer">
					<%--Cancel--%>
					<button type="button" class="btn btn-secondary" data-dismiss="modal">
						<span><op:translate key="CANCEL" /></span>
					</button>

					<%--Delete--%>
					<button type="button" class="btn btn-primary" data-submit>
						<span><op:translate key="DISCUSSION_DELETE" /></span>
					</button>
				</div>
			</div>
		</div>
	</div>


	<%--Report modal confirmation--%>
	<div id="${reportMessageConfirmationModalId}" class="modal fade" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">
						<op:translate key="DISCUSSION_REPORT_MESSAGE_CONFIRMATION_MODAL_BODY" />
					</h5>
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
				</div>

				<div class="modal-body">
					<p>
						<op:translate key="DISCUSSION_REPORT_MESSAGE_CONFIRMATION_MODAL_DETAIL" />
					</p>
					<form action="${reportMessageUrl}" method="post">
						<input type="hidden" name="messageIndice"> <input type="submit" class="d-none">
					</form>
				</div>

				<div class="modal-footer">
					<%--Cancel--%>
					<button type="button" class="btn btn-secondary" data-dismiss="modal">
						<span><op:translate key="CANCEL" /></span>
					</button>

					<%--Delete--%>
					<button type="button" class="btn btn-primary" data-submit>
						<span><op:translate key="DISCUSSION_REPORT" /></span>
					</button>
				</div>
			</div>
		</div>
	</div>


	<!-- Delete discussion modal -->
	<div id="deleteDiscussionConfirmationModalId" class="modal fade" tabindex="-1" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">

				<div class="modal-header">
					<h5 class="modal-title">
						<op:translate key="DISCUSSION_DELETE_CURRENT_MODAL_BODY" />
					</h5>
					<button type="button" class="close" data-dismiss="modal">
						<span>&times;</span>
					</button>
				</div>

				<div class="modal-body">
					<p>
						<op:translate key="DISCUSSION_DELETE_CURRENT_MODAL_MESSAGE" />
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">
						<span><op:translate key="CANCEL" /></span>
					</button>

					<a href="${deleteDiscussionUrl}" class="btn btn-primary" data-dismiss="modal"> <span><op:translate
								key="DISCUSSION_DELETE" /></span>
					</a>
				</div>
			</div>
		</div>
	</div>


</div>


