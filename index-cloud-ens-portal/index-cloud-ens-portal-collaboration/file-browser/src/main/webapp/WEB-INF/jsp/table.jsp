<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>
<%@ taglib uri="http://www.toutatice.fr/jsp/taglib/toutatice" prefix="ttc" %>


<%--Table--%>
<div class="portal-table overflow-x-hidden">
    <%--Header--%>
    <%@ include file="table-header.jspf" %>

    <div class="portlet-filler overflow-y-auto overflow-x-hidden">
        <%--Body--%>
        <%@ include file="table-body.jspf" %>
    </div>
</div>
