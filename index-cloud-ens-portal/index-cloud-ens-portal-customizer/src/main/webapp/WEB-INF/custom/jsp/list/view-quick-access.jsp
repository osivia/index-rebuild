<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<div class="list-quick-access">
    <div class="card-deck">
        <c:forEach var="document" items="${documents}" varStatus="status">
            <c:choose>
                <c:when test="${status.count < 2}"><c:set var="display" value="d-flex"/></c:when>
                <c:when test="${status.count < 3}"><c:set var="display" value="d-none d-sm-flex"/></c:when>
                <c:when test="${status.count < 4}"><c:set var="display" value="d-none d-md-flex"/></c:when>
                <c:when test="${status.count < 5}"><c:set var="display" value="d-none d-xl-flex"/></c:when>
                <c:otherwise><c:set var="display" value="d-none"/></c:otherwise>
            </c:choose>

            <div class="card card-custom card-custom-border-bottom card-custom-hover card-custom-${document.properties['mtz:enable'] ? 'orange' : 'green'} ${display} shadow">
                <div class="card-body d-flex flex-column pb-2">
                    <%--Badges--%>
                    <div class="card-custom-badges">
                        <%--PRONOTE indicator--%>
                        <c:if test="${not empty document.properties['rshr:targets']}">
                            <c:set var="title"><op:translate key="TOOLTIP_PRONOTE"/></c:set>
                            <img src="/index-cloud-ens-charte/img/pronote-indicator.svg" alt="PRONOTE" title="${title}" height="16">
                        </c:if>
                        
                        <%--SHARED indicator--%>
                        <c:if test="${ empty document.properties['rshr:targets'] and document.properties['rshr:enabledLink']}">
                            <c:set var="title"><op:translate key="TOOLTIP_SHARED"/></c:set>
                            <i class="glyphicons glyphicons-basic-paired customized-icon-shared-small" title="${title}"></i>
                        </c:if>                        

                        <%--Mutualized document--%>
                        <c:if test="${document.properties['mtz:enable']}">
                            <c:set var="title"><op:translate key="TOOLTIP_MUTUALIZED"/></c:set>
                            <i title="${title}" class="glyphicons glyphicons-basic-share text-orange"></i>
                        </c:if>
                    </div>

                    <%--Icon--%>
					<div class="my-1 d-flex justify-content-center align-items-center" style="height: 160px">
						<c:set var="vignetteUrl">
							<ttc:pictureLink document="${document}" property="ttc:vignette" />
						</c:set>
						<c:choose>
							<c:when test="${not empty vignetteUrl}">
								<img src="${vignetteUrl}" alt="" class="p-0 img-fluid mh-100">
							</c:when>

							<c:when test="${document.type.name eq 'Picture'}">
								<c:set var="pictureUrl">
									<ttc:documentLink document="${document}" picture="true" displayContext="Small" />
								</c:set>
								<img src="${pictureUrl}" alt="" class="p-0 img-fluid mh-100" >
							</c:when>

							<c:otherwise>
								<div class="card-custom-icon card-custom-icon-lg">
									<ttc:icon document="${document}" />
								</div>
							</c:otherwise>
						</c:choose>
					</div>



                    <%--Title--%>
                    <h3 class="card-title mb-0 text-truncate">
                        <c:set var="url"><ttc:documentLink document="${document}"/></c:set>
                        <a href="${url}" title="${document.title}" class="stretched-link text-black text-decoration-none no-ajax-link">
                            <span>${document.title}</span>
                        </a>
                    </h3>

                    <c:if test="${not empty document.properties['dc:modified']}">
                        <div class="text-truncate">
                            <small class="text-muted">
                                <span><op:translate key="LIST_TEMPLATE_QUICK_ACCESS_MODIFIED_ON"/></span>
                                <span><op:formatRelativeDate value="${document.properties['dc:modified']}" tooltip="false"/></span>
                            </small>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
