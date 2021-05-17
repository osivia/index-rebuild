<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>


<div class="file-browser-thumbnails-container file-browser-selectable">
    <div class="portlet-filler">
        <div class="file-browser-folders">
            <h3 class="h4"><op:translate key="FILE_BROWSER_FOLDERS" /></h3>

            <c:set var="count" value="0" />
            
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-5">
                <c:forEach var="item" items="${form.items}">
                    <c:if test="${item.folderish}">
                        <c:set var="count" value="${count + 1}" />
                        <c:set var="url"><ttc:documentLink document="${item.document}" /></c:set>

                        <div class="col">
                            <div class="file-browser-thumbnail file-browser-selectable-filter file-browser-droppable" data-id="${item.document.id}"
                                    data-type="${item.document.type.name}" data-text="${item.title}" data-accepted-types="${item.acceptedTypes}"
                                    data-double-click-target=".file-browser-draggable a">
                                <%--Title--%>
                                <div class="file-browser-thumbnail-title">
                                    <div class="text-truncate file-browser-draggable">
                                        <span class="mr-1"><ttc:icon document="${item.document}" /></span>
                                        <a href="${url}" title="${item.document.title}" class="text-black no-ajax-link">
                                            <strong>${item.document.title}</strong>
                                        </a>
                                    </div>
                                </div>
                                
                                <%--Draggable--%>
                                <c:if test="${item.document.type.editable}">
                                    <div class="file-browser-draggable file-browser-draggable-shadowbox border-primary"></div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
            
            <c:if test="${count eq 0}">
                <p class="text-muted pb-3"><op:translate key="FILE_BROWSER_EMPTY_FOLDER" /></p>
            </c:if>
        </div>
        
        <div class="file-browser-files">
            <h3 class="h4"><op:translate key="FILE_BROWSER_FILES" /></h3>
    
            <c:set var="count" value="0" />
            
            <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-5">
                <c:forEach var="item" items="${form.items}">
                    <c:if test="${not item.folderish}">
                        <c:set var="count" value="${count + 1}" />
                        <c:set var="url"><ttc:documentLink document="${item.document}" /></c:set>
                        
                        <div class="col">
                            <div class="file-browser-thumbnail file-browser-selectable-filter" data-id="${item.document.id}" 
                                    data-type="${item.document.type.name}" data-text="${item.title}"
                                    data-double-click-target=".file-browser-draggable a" data-mutualized="${item.mutualized}">
                                <%--Preview--%>
                                <div class="file-browser-thumbnail-preview-container">
                                    <div class="file-browser-thumbnail-preview">
                                        <c:set var="vignetteUrl"><ttc:pictureLink document="${item.document}" property="ttc:vignette" /></c:set>
                                        <c:choose>
                                            <c:when test="${not empty vignetteUrl}">
                                                <img src="${vignetteUrl}" alt="" class="vignette">
                                            </c:when>
                                        
                                            <c:when test="${item.document.type.name eq 'Picture'}">
                                                <c:set var="pictureUrl"><ttc:documentLink document="${item.document}" picture="true" displayContext="Small" /></c:set>
                                                <img src="${pictureUrl}" alt="" class="picture">
                                            </c:when>
                                        
                                            <c:otherwise>
                                                <div class="file-browser-thumbnail-icon-preview card-custom-icon card-custom-icon-lg">
                                                    <ttc:icon document="${item.document}" />
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <%--Title--%>
                                <div class="file-browser-thumbnail-title">
                                    <div class="text-truncate file-browser-draggable">
                                        <a href="${url}" title="${item.document.title}" class="text-black no-ajax-link">
                                            <strong>${item.document.title}</strong>
                                        </a>
                                    </div>
                                </div>
                                
                                <%--Draggable--%>
                                <c:if test="${item.document.type.editable}">
                                    <div class="file-browser-draggable file-browser-draggable-shadowbox"></div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
            
            <c:if test="${count eq 0}">
                <p class="text-muted"><op:translate key="FILE_BROWSER_EMPTY_FILE" /></p>
            </c:if>
        </div>
    </div>
</div>
