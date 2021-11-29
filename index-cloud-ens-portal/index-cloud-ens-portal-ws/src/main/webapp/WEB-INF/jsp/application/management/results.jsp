<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<table class="table table-hover">
    <%--Table header--%>
    <thead>
        <tr>
            <th>
                <c:choose>
                    <c:when test="${applicationForm.maxResults}">
                        <span><op:translate key="APPLICATION_MANAGEMENT_TOO_MANY_RESULTS" /></span>
                    </c:when>

                    <c:when test="${empty applicationForm.applications}">
                        <span><op:translate key="APPLICATION_MANAGEMENT_NO_RESULT" /></span>
                    </c:when>

                    <c:when test="${fn:length(applicationForm.applications) eq 1}">
                        <span><op:translate key="APPLICATION_MANAGEMENT_ONE_RESULT" /></span>
                    </c:when>

                    <c:otherwise>
                        <span><op:translate key="APPLICATION_MANAGEMENT_MULTIPLE_RESULTS" args="${fn:length(applicationForm.applications)}"/></span>
                    </c:otherwise>
                </c:choose>
            </th>
        </tr>
    </thead>
    
    <%--Table body--%>
    <c:if test="${not empty applicationForm.applications}">
        <tbody>
            <c:forEach items="${applicationForm.applications}" var="application">
                <tr>
                    <td class="position-relative ${application.id eq applicationForm.selectedApplicationId ? 'table-active' : ''}">
                        <div class="d-flex">

                            <div class="d-flex flex-grow-1 flex-column">
                                <%--Display name--%>
                                <a href="javascript:" class="stretched-link no-ajax-link" data-id="${application.id}">
                                    <span>${application.title}</span>
                                </a>
                            </div>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </c:if>
</table>
