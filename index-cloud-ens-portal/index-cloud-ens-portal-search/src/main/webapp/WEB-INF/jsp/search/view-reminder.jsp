<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<ul class="list-inline">
    <c:choose>
        <c:when test="${empty form.reminder}">
            <li class="list-inline-item">
                <span class="text-muted">&nbsp;</span>
            </li>
        </c:when>

        <c:otherwise>
            <c:forEach var="item" items="${form.reminder}">
                <li class="list-inline-item">
                    <a href="#" class="btn btn-outline-secondary btn-sm disabled">
                        <span>${item}</span>
                    </a>
                </li>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</ul>
