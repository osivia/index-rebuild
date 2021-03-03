<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false" %>


<c:set var="breadcrumb" value="${requestScope['osivia.breadcrumb']}"/>
<c:set var="activeSavedSearch" value="${requestScope['osivia.saved-search.active']}"/>


<nav class="d-none d-md-block">
    <h2 class="sr-only"><op:translate key="BREADCRUMB_TITLE"/></h2>
    <ol class="breadcrumb">
        <c:choose>
            <c:when test="${empty activeSavedSearch}">
                <c:forEach var="child" items="${breadcrumb.children}" varStatus="status">
                    <c:choose>
                        <c:when test="${status.first and requestScope['osivia.breadcrumb.hide-first']}">
                            <%--Hide user workspace home--%>
                        </c:when>

                        <c:when test="${status.last}">
                            <li class="breadcrumb-item active">
                                <strong>${child.name}</strong>
                            </li>
                        </c:when>

                        <c:otherwise>
                            <li class="breadcrumb-item">
                                <a href="${child.url}">
                                    <strong>${child.name}</strong>
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </c:when>

            <c:otherwise>
                <li class="breadcrumb-item active">
                    <strong>${activeSavedSearch}</strong>
                </li>
            </c:otherwise>
        </c:choose>
    </ol>
</nav>
