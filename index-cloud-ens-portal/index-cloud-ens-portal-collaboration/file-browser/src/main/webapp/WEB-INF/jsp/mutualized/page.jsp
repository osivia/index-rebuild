<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="op" uri="http://www.osivia.org/jsp/taglib/osivia-portal" %>
<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>




<c:forEach var="item" items="${items}">
    <%@ include file="table-row.jspf" %>
</c:forEach>

<c:if test="${nextPageIndex gt 0}">
    <portlet:actionURL name="save-position" var="savePositionUrl">
        <portlet:param name="pageIndex" value="${nextPageIndex}"/>
    </portlet:actionURL>
    <portlet:resourceURL id="load-page" var="loadPageUrl">
        <portlet:param name="pageIndex" value="${nextPageIndex}"/>
    </portlet:resourceURL>

    <%@ include file="more.jspf" %>
</c:if>
