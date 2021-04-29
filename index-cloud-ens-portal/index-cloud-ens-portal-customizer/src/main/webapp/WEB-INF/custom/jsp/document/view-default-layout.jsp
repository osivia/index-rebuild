<%@ taglib prefix="ttc" uri="http://www.toutatice.fr/jsp/taglib/toutatice" %>

<%@ page isELIgnored="false" %>


<div class="portlet-filler d-flex flex-column flex-grow-1">
    <ttc:include page="toolbar.jsp"/>

    <div class="row flex-lg-grow-1 flex-lg-nowrap">
        <div class="col-lg-7 col-xl-8 d-lg-flex flex-column overflow-auto mb-4 mb-lg-0">
            <div class="d-flex flex-column flex-grow-1 flex-shrink-0 bg-white rounded">
                <ttc:include page="view-${dispatchJsp}.jsp"/>
            </div>
        </div>

        <div class="col-lg-5 col-xl-4 d-lg-flex flex-column overflow-auto">
            <%--Document attachments view--%>
            <c:if test="${attachments}">
                <ttc:include page="attachments.jsp"/>
            </c:if>

            <%--Metadata--%>
            <c:if test="${metadata}">
                <ttc:include page="metadata.jsp"/>
            </c:if>
        </div>
    </div>
</div>
