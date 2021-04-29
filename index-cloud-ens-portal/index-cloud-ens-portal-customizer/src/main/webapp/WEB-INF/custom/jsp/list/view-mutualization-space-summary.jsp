<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<div class="list-mutualization-space-summary">
    <c:if test="${empty documents}">
        <span class="text-muted"><op:translate key="DOCUMENT_NO_MUTUALIZATION" /></span>
    </c:if>

    <c:forEach var="document" items="${documents}">
        <div class="card card-custom card-custom-border-left card-custom-hover card-custom-${document.properties['mtz:enable'] ? 'orange' : 'green'} mb-3">
            <div class="card-body py-3">
                <%--Badges--%>
                <div class="card-custom-badges">
                    <%--Mutualized document--%>
                    <c:if test="${document.properties['mtz:enable']}">
                        <c:set var="title"><op:translate key="TOOLTIP_MUTUALIZED"/></c:set>
                        <i title="${title}" class="glyphicons glyphicons-basic-share text-orange"></i>
                    </c:if>
                </div>

                <div class="d-flex align-items-center">
                    <div class="flex-shrink-0 mr-3">
                        <%--Icon--%>
                        <div class="card-custom-icon">
                            <ttc:icon document="${document}" />
                        </div>
                    </div>

                    <div class="flex-grow-1 flex-shrink-1">
                        <%--Title--%>
                        <h3 class="card-title mb-0 text-truncate">
                            <c:set var="url"><ttc:documentLink document="${document}" /></c:set>
                            <a href="${url}" title="${document.displayTitle}" class="stretched-link text-black text-decoration-none no-ajax-link">
                                <span>${document.displayTitle}</span>
                            </a>
                        </h3>


                        <div class="text-truncate">
                            <small class="text-muted">
                                <span><op:translate key="LIST_TEMPLATE_QUICK_ACCESS_MODIFIED_ON"/></span>
                                <c:choose> 
                                       <c:when test="${not empty document.properties['dc:issued']}"> 
                                             <span><op:formatRelativeDate value="${document.properties['dc:issued']}" tooltip="false"/></span>
									    </c:when> 
									    <c:otherwise>           
									       <span><op:formatRelativeDate value="${document.properties['dc:modified']}" tooltip="false"/></span>  
									    </c:otherwise>   
				                </c:choose>                   
                            </small>
                        </div>

                    </div>
                </div>

                <%--Informations--%>
                <div class="card-custom-informations">
                    <ul class="list-inline mb-0 text-secondary">
                        <%--Views--%>
                        <li class="list-inline-item">
                            <i class="glyphicons glyphicons-basic-eye"></i>
                            <strong class="text-black">${document.properties['mtz:liveviews']}</strong>
                            <c:choose>
                                <c:when test="${empty document.properties['mtz:liveviews'] or document.properties['mtz:liveviews'] le 1}">
                                    <span><op:translate key="DOCUMENT_MUTUALIZATION_VIEW" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span><op:translate key="DOCUMENT_MUTUALIZATION_VIEWS" /></span>
                                </c:otherwise>
                            </c:choose>
                        </li>

                        <%--Downloads--%>
                        <li class="list-inline-item">
                            <i class="glyphicons glyphicons-basic-cloud-download"></i>
                            <strong class="text-black">${document.properties['mtz:livedownloads']}</strong>
                            <c:choose>
                                <c:when test="${empty document.properties['mtz:livedownloads'] or document.properties['mtz:livedownloads'] le 1}">
                                    <span><op:translate key="DOCUMENT_MUTUALIZATION_DOWNLOAD" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span><op:translate key="DOCUMENT_MUTUALIZATION_DOWNLOADS" /></span>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
