<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />


<div class="my-messages home-fixed-block">
	<c:choose>
		<c:when test="${empty unreadMessages.items}">
			<span class="text-muted"><op:translate key="DISCUSSION_NO_UNREAD_MESSAGE"/></span>
		</c:when>

		<c:otherwise>
			<c:forEach var="message" items="${unreadMessages.items}" varStatus="counter">
			
			<c:choose>
		        <c:when test="${counter.index mod 2 == 0}">
		            <c:set var="style" value="left" />
		        </c:when>
		        <c:otherwise>
		            <c:set var="style" value="right" />
		        </c:otherwise>
		    </c:choose> 
			
			

	
	           <div class="card card-custom mb-3 home-fixed-block-item rounded-pill triangle-${style} }">
                    <div class="card-body p-2">
                        <div class="d-flex align-items-center">
                            <div class="mr-3">
                                <%--Icon--%>
                                <div class="card-custom-icon">
                                    <c:if test="${not empty message.task.publicationDTO}">       
                                        <ttc:icon document="${message.task.publicationDTO}" />
                                    </c:if> 
                                    <c:if test="${empty message.task.publicationDTO}">
                                          <div class="avatar">
                                                <ttc:user name="${message.task.properties['author']}" hideDisplayName="true" linkable="false" />
                                           </div>
                                    </c:if> 
                                </div>
                            </div>
                            <div class="flex-grow-1 flex-shrink-1">
                                <%--Title--%>
                                <h3 class="card-title mb-0 text-truncate">
                                    <a href="${message.url}" class="stretched-link text-black text-decoration-none no-ajax-link">
                                        <span>${message.task.properties['discussionTitle']}</span>
                                    </a>                                        
                                </h3>
                                <div class="card-text text-truncate"><small>${message.task.properties['message']}</small></div>
                            </div>
                        </div>
                     </div>
                  </div>
                     

			
			
			
			
			</c:forEach>
		</c:otherwise>
	</c:choose>
</div>
