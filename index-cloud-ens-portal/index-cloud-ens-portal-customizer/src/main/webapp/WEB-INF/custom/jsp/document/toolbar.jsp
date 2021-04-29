<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>




<div class="d-flex justify-content-between flex-wrap">
    <div class="d-flex mt-1 mb-2">
        <%--Icon--%>
        <div class="flex-shrink-0 mr-2">
            <ttc:icon document="${document}"/>
        </div>

        <%--Title--%>
        <div class="flex-shrink-1 mr-2 text-truncate">
            <strong>${document.displayTitle}</strong>
        </div>

        <%--Size--%>
        <c:if test="${document.type.file}">
            <c:set var="size" value="${document.properties['file:content']['length']}"/>
            <div class="flex-shrink-0 mr-2">
                <small class="text-secondary"><ttc:fileSize size="${size}"/></small>
            </div>
        </c:if>

        <%--Mutualized indicator--%>
        <c:if test="${not readOnly and document.properties['mtz:enable']}">
            <c:set var="title"><op:translate key="TOOLTIP_MUTUALIZED"/></c:set>
            <div class="flex-shrink-0 mr-2">
                <span class="text-orange" title="${title}">
                    <i class="glyphicons glyphicons-basic-share"></i>
                </span>
            </div>
        </c:if>
    </div>

    <div class="d-flex justify-content-end flex-grow-1 flex-wrap mb-4 ml-1">
        <%--Mutualize--%>
        <c:if test="${not readOnly and not empty mutualizeUrl}">
            <c:set var="title"><op:translate key="DOCUMENT_FILE_TOOLBAR_MUTUALIZE"/></c:set>
            <c:set var="titleOver"><op:translate key="DOCUMENT_FILE_TOOLBAR_MUTUALIZE_OVER"/></c:set>
            
            <a href="javascript:" title="${titleOver}" class="btn btn-link btn-link-hover-orange btn-sm mb-1 ml-1 text-orange-dark no-ajax-link" data-target="#osivia-modal" data-load-url="${mutualizeUrl}" data-title="${title}">
                <i class="glyphicons glyphicons-basic-share"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>

        <%--Copy--%>
        <c:if test="${readOnly and not empty copyUrl}">
            <c:set var="title"><op:translate key="DOCUMENT_FILE_TOOLBAR_COPY"/></c:set>
            <a href="javascript:" title="${title}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark no-ajax-link" data-target="#osivia-modal" data-load-url="${copyUrl}" data-title="${title}">
                <i class="glyphicons glyphicons-basic-copy-duplicate"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>

        <%--Share--%>
        <c:if test="${not readOnly}">
        
            <c:set var="confirmation" value="${false}" />
        
            <c:choose>
                <c:when test="${document.properties['rshr:enabledLink']}">
                      <portlet:actionURL name="link-activation" var="activationUrl">
                        <portlet:param name="activate" value="false"/>
                    </portlet:actionURL>
                    <c:set var="icon" value="glyphicons glyphicons-basic-paired-off" />
                    <c:set var="title"><op:translate key="DOCUMENT_FILE_TOOLBAR_SHARED_LINK_DEACTIVATE"/></c:set>
                    <c:set var="titleOver"><op:translate key="DOCUMENT_FILE_TOOLBAR_SHARED_LINK_DEACTIVATE"/></c:set>
                    <c:set var="targets" value="${document.properties['rshr:targets']}"/>    
                    <c:if test="${not empty targets}">    
                        <c:set var="confirmation" value="${true}" />
                    </c:if>
                </c:when>
                <c:otherwise>
                    <portlet:actionURL name="link-activation" var="activationUrl">
                        <portlet:param name="activate" value="true"/>
                    </portlet:actionURL>
                    <c:set var="icon" value="glyphicons glyphicons-basic-paired" />
                    <c:set var="title"><op:translate key="DOCUMENT_FILE_TOOLBAR_SHARED_LINK_ACTIVATE"/></c:set>
                    <c:set var="titleOver"><op:translate key="DOCUMENT_FILE_TOOLBAR_SHARED_LINK_ACTIVATE_OVER"/></c:set>
                </c:otherwise>
            </c:choose>
            
            
             <c:choose>
                <c:when test="${confirmation}">       
                    <a title="${titleOver}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark  no-ajax-link" href="#deactivateShareLinkModalId" data-toggle="modal">
                        <i class="${icon}"></i>
                        <strong class="d-none d-md-inline">${title}</strong>
                    </a>
                 </c:when>
                <c:otherwise>
  		            <a href="${activationUrl}" title="${titleOver}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark">
		                <i class="${icon}"></i>
		                <strong class="d-none d-md-inline">${title}</strong>
		            </a>
                 </c:otherwise>          
            </c:choose>

            
        </c:if>

        <%--Download--%>
        <c:if test="${document.type.file}">
            <c:set var="url"><ttc:documentLink document="${document}" displayContext="download"/></c:set>
            <c:set var="title"><op:translate key="DOWNLOAD"/></c:set>
            <a href="${url}" target="_blank" title="${title}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark no-ajax-link">
                <i class="glyphicons glyphicons-basic-arrow-thin-down"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>

        <%--Rename--%>
        <c:if test="${not readOnly and not empty renameUrl}">
            <c:set var="title"><op:translate key="DOCUMENT_RENAME"/></c:set>
            <a href="javascript:" title="${title}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark no-ajax-link" data-target="#osivia-modal"
               data-load-url="${renameUrl}" data-title="${title}">
                <i class="glyphicons glyphicons-basic-square-edit"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>

        <%--Edit--%>
        <c:if test="${not readOnly and not empty editUrl}">
            <c:set var="title"><op:translate key="DOCUMENT_EDIT"/></c:set>
            <a href="javascript:" title="${title}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark no-ajax-link" data-target="#osivia-modal"
               data-load-url="${editUrl}" data-title="${title}">
                <i class="glyphicons glyphicons-basic-refresh"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>

        <%--Delete--%>
        <c:if test="${not readOnly and not empty deleteUrl}">
            <c:set var="title"><op:translate key="DELETE"/></c:set>
            <a href="javascript:" title="${title}" class="btn btn-link btn-link-hover-primary-light btn-sm mb-1 ml-1 text-primary-dark no-ajax-link" data-target="#osivia-modal" data-load-url="${deleteUrl}" data-title="${title}">
                <i class="glyphicons glyphicons-basic-bin"></i>
                <strong class="d-none d-md-inline">${title}</strong>
            </a>
        </c:if>
    </div>
</div>



    <!-- Deactivate share link modal -->
    <div id="deactivateShareLinkModalId" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">

                <div class="modal-header">
                    <h5 class="modal-title">
                        <op:translate key="DEACTIVATION_DELETE_CURRENT_MODAL_BODY" />
                    </h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span>&times;</span>
                    </button>
                </div>

                <div class="modal-body">
                    <p>
                        <op:translate key="DEACTIVATION_DELETE_CURRENT_MODAL_MESSAGE" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">
                        <span><op:translate key="CANCEL" /></span>
                    </button>

                    <a href="${activationUrl}" class="btn  btn-warning" data-dismiss="modal"> <span><op:translate
                                key="CONFIRM" /></span>
                    </a>
                </div>
            </div>
        </div>
    </div>


