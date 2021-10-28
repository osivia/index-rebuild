<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice" prefix="ttc" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<c:set var="folder" value="${parent}" />


<c:if test="${not empty folder.children}">
    <ul class="ml-0">
        <c:forEach var="child" items="${folder.children}">
            <li class="${child.active ? 'current' : ''}" data-retain="${child.selected}" data-acceptedtypes="${fn:join(child.acceptedTypes, ',')}" data-expanded="${child.selected}" data-folder="${child.folder}" data-lazy="${child.lazy}" data-current="${child.active}" data-id="${child.id}" data-path="${child.path}">
                
                <c:if test="${not parent.selected}">
                    <c:set var="hideStyle" value="d-none" scope="page" />
                </c:if>
                
                
                <a href="${child.url}" class="${hideStyle} pl-3">
                    <span>${child.displayName}</span>
                </a>
            
                <!-- Children -->
                <c:set var="parent" value="${child}" scope="request" />
                <ttc:include page="folder-children.jsp" />
            </li>
        </c:forEach>
    </ul>
</c:if>
